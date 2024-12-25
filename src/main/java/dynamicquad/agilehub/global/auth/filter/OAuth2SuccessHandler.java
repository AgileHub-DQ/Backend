package dynamicquad.agilehub.global.auth.filter;

import static dynamicquad.agilehub.global.auth.repository.CustomAuthorizationRequestRepository.REDIRECT_URL_PARAM_COOKIE_NAME;

import dynamicquad.agilehub.global.auth.model.GeneratedToken;
import dynamicquad.agilehub.global.auth.model.JwtRefreshToken;
import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.global.auth.repository.CustomAuthorizationRequestRepository;
import dynamicquad.agilehub.global.auth.service.RefreshTokenRedisService;
import dynamicquad.agilehub.global.auth.util.CookieUtil;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final CustomAuthorizationRequestRepository customAuthorizationRequestRepository;

    private final RefreshTokenRedisService redisService;

    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        GeneratedToken generatedToken = generateMemberToken(authentication);

        String redirectUrl = getRedirectUrl(targetUrl, generatedToken);
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URL_PARAM_COOKIE_NAME)
            .map(cookie -> cookie.getValue());

        clearAuthenticationAttributes(request, response);
        return redirectUri.orElse(getDefaultTargetUrl());
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        customAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String getRedirectUrl(String targetUrl, GeneratedToken generatedToken) {
        return UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("accessToken", generatedToken.getAccessToken())
            .build().toUriString();
    }

    private GeneratedToken generateMemberToken(Authentication authentication) {
        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        String provider = principal.getProvider();
        String distinctId = principal.getId();
        String name = principal.getName();
        String role = principal.getAuthorities().stream().findFirst()
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_ROLE_NOT_EXIST)).getAuthority();

        GeneratedToken token = jwtUtil.generateToken(name, role, provider, distinctId);

        redisService.save(new JwtRefreshToken(token.getAccessToken(), token.getRefreshToken()));

        return token;
    }

}
