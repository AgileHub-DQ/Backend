package dynamicquad.agilehub.global.filter;

import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.global.auth.util.CookieUtil;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.exception.JwtException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.service.MemberQueryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    private final MemberQueryService memberQueryService;

    /*
     * access token 유효 -> authentication 저장
     * access token 만료 -> 401 Unauthorized 예외 발생
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtUtil.extractAccessToken(request)
                .filter(jwtUtil::verifyToken)
                .orElseThrow(() -> new JwtException(ErrorStatus.INVALID_ACCESS_TOKEN.getMessage()));

        saveAuthentication(token);
        filterChain.doFilter(request, response);
    }

    private void saveAuthentication(String accessToken) {
        String provider = jwtUtil.extractProvider(accessToken);
        String distinctId = jwtUtil.extractDistinctId(accessToken);

        Member findMember = memberQueryService.findBySocialProviderAndDistinctId(provider, distinctId);
        SecurityMember securityMember = new SecurityMember(findMember);

        SecurityContextHolder.getContext().setAuthentication(getAuthentication(securityMember));
    }

    private Authentication getAuthentication(SecurityMember securityMember) {
        return new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().contains("/api/auth/token/refresh");
    }
}
