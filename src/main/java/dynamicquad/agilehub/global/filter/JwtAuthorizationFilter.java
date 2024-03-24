package dynamicquad.agilehub.global.filter;

import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberService memberService;

    /**
     * access token 유효 -> authentication 저장
     * access token 만료
     *  - refresh token 유효 -> access token 재발급, authentication 저장
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

    }

    private void saveAuthentication(String accessToken) {

    }

}
