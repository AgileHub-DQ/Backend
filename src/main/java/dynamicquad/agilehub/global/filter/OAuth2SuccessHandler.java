package dynamicquad.agilehub.global.filter;

import static dynamicquad.agilehub.global.auth.repository.CookieAuthorizationRequestRepository.REDIRECT_URI_COOKIE_NAME;

import dynamicquad.agilehub.global.auth.model.GeneratedToken;
import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.global.auth.repository.CookieAuthorizationRequestRepository;
import dynamicquad.agilehub.global.auth.util.CookieUtil;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.auth.util.RedisUtil;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final CookieUtil cookieUtil;

    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2SuccessHandler.onAuthenticationSuccess");

        GeneratedToken generatedToken = generateMemberToken(authentication);

        String redirectUrl = determineTargetUrl(request, response);
        getRedirectStrategy().sendRedirect(request, response, getRedirectUrlWithJwtToken(redirectUrl, generatedToken));
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        Optional<String> redirectUri = cookieUtil.getCookie(request, REDIRECT_URI_COOKIE_NAME).map(Cookie::getValue);

        //이미 OAuth2LoginAuthenticationFilter에서 authentication을 꺼내왔고 위에서 redirectUrl을 받아왔으므로 쿠키의 값은 제거하면 된다
        clearAuthenticationAttributes(request, response);
        return redirectUri.orElse(getDefaultTargetUrl());
    }

    private GeneratedToken generateMemberToken(Authentication authentication) {
        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        String provider = principal.getProvider();
        String distinctId = principal.getId();
        String name = principal.getName();
        String role = principal.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_ROLE_NOT_EXIST))
                .getAuthority();

        GeneratedToken token = jwtUtil.generateToken(name, role, provider, distinctId);

        String data = redisUtil.getData(token.getRefreshToken());
        if (data != null && !data.isEmpty()) {
            redisUtil.deleteData(token.getRefreshToken());
        }
        redisUtil.setDataExpire(token.getRefreshToken(), token.getAccessToken(),
                jwtUtil.getRefreshTokenValidationSeconds());

        return token;
    }

    private String getRedirectUrlWithJwtToken(String redirectUrl, GeneratedToken token) {
        return UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam(jwtUtil.getAccessHeader(), token.getAccessToken())
                .queryParam(jwtUtil.getRefreshHeader(), token.getRefreshToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

}
