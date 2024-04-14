package dynamicquad.agilehub.global.filter;

import dynamicquad.agilehub.global.auth.model.GeneratedToken;
import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.global.auth.service.RefreshTokenRedisService;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final String REDIRECT_URL = "/auth/success";

    private final RefreshTokenRedisService redisService;

    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2SuccessHandler.onAuthenticationSuccess");

        GeneratedToken generatedToken = generateMemberToken(authentication);

        String redirectUrl = UriComponentsBuilder.fromUriString(REDIRECT_URL)
                .queryParam("accessToken", generatedToken.getAccessToken())
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }

    private GeneratedToken generateMemberToken(Authentication authentication) {
        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        String provider = principal.getProvider();
        String distinctId = principal.getId();
        String name = principal.getName();
        String role = principal.getAuthorities().stream().findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_ROLE_NOT_EXIST)).getAuthority();

        GeneratedToken token = jwtUtil.generateToken(name, role, provider, distinctId);

        redisService.save(token.getRefreshToken(), token.getAccessToken());

        return token;
    }

}
