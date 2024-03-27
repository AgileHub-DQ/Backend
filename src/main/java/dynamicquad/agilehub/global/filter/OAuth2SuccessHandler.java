package dynamicquad.agilehub.global.filter;

import dynamicquad.agilehub.global.auth.model.GeneratedToken;
import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.service.SocialLoginQueryService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final SocialLoginQueryService socialLoginQueryService;

    /*
     * TODO
     * OAuth2 로그인 성공 시, 유저의 token을 업데이트할 로직 작성 중.
     * 다만 Redis를 사용하여 토큰을 저장하는 로직이 필요함.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        GeneratedToken generatedToken = generateMemberToken(authentication);
    }

    private GeneratedToken generateMemberToken(Authentication authentication) {
        SecurityMember principal = (SecurityMember) authentication.getPrincipal();
        String name = principal.getName();
        String role = principal.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_ROLE_NOT_EXIST))
                .getAuthority();

        return jwtUtil.generateToken(name, role);
    }

}
