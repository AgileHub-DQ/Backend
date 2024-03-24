package dynamicquad.agilehub.global.filter;

import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.member.service.SocialLoginQueryService;
import dynamicquad.agilehub.member.service.SocialLoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final SocialLoginQueryService socialLoginQueryService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        String name = principal.getName();

        socialLoginQueryService.findByProviderAndDistinctCode()
    }
}
