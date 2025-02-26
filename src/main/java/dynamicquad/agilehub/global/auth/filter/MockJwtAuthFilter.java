package dynamicquad.agilehub.global.auth.filter;

import dynamicquad.agilehub.global.auth.service.RefreshTokenRedisService;
import dynamicquad.agilehub.global.auth.util.JwtUtil;
import dynamicquad.agilehub.member.service.MemberQueryService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"security-off", "test", "local"})
public class MockJwtAuthFilter extends JwtAuthFilter {

    public MockJwtAuthFilter(JwtUtil jwtUtil,
                             MemberQueryService memberQueryService,
                             RefreshTokenRedisService redisService) {
        super(jwtUtil, memberQueryService, redisService);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        filterChain.doFilter(request, response);
    }

}
