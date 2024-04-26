package dynamicquad.agilehub.global.filter;

import dynamicquad.agilehub.global.auth.model.JwtRefreshToken;
import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.global.auth.service.RefreshTokenRedisService;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.global.exception.JwtException;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.service.MemberQueryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final MemberQueryService memberQueryService;
    private final RefreshTokenRedisService redisService;

    /*
     * access token 유효 -> authentication 저장
     * access token 만료 -> 401 Unauthorized 예외 발생
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        Optional<String> token = jwtUtil.extractAccessToken(request);

        if (token.isEmpty()) {
            throw new JwtException("Access Token is empty");
        }

        String accessToken = token.get();
        if (jwtUtil.verifyToken(accessToken)) {
            saveAuthentication(accessToken);
        } else {
            String reissuedAccessToken = reissueAccessToken(accessToken);
            if (StringUtils.hasText(reissuedAccessToken)) {
                saveAuthentication(reissuedAccessToken);
                response.setHeader(jwtUtil.getAccessHeader(), reissuedAccessToken);
            } else {
                throw new JwtException("Refresh Token is expired");
            }
        }
        filterChain.doFilter(request, response);
    }

    private String reissueAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            JwtRefreshToken jwtRefreshToken = redisService.findByAccessToken(accessToken);
            String refreshToken = jwtRefreshToken.getRefreshToken();

            if (jwtUtil.verifyToken(refreshToken)) {
                String name = jwtUtil.extractName(accessToken);
                String role = jwtUtil.extractRole(accessToken);
                String provider = jwtUtil.extractProvider(accessToken);
                String distinctId = jwtUtil.extractDistinctId(accessToken);

                String reissuedAccessToken = jwtUtil.generateAccessToken(name, role, provider, distinctId);
                jwtRefreshToken.updateAccessToken(reissuedAccessToken);
                redisService.save(jwtRefreshToken);

                return reissuedAccessToken;
            }
        }
        return null;
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
    protected boolean shouldNotFilter(HttpServletRequest request) {
        log.warn("request.getRequestURI() : {}", request.getRequestURI());
        //return request.getRequestURI().contains("/oauth2");
        String path = request.getRequestURI();
        return path.contains("/oauth2") || path.contains("/api/api-docs") || path.contains("/api/v3/api-docs")
            || path.startsWith("/api/swagger-ui");
    }
}
