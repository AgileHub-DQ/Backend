package dynamicquad.agilehub.global.auth;

import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.member.domain.Member;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MockSecurityContext {
    @PostConstruct
    public void init() {
        // 테스트용 Member 객체 생성
        Member mockMember = Member.builder()
            .name("테스트사용자")
            .profileImageUrl("http://example.com/test.jpg")
            .build();

        // SecurityMember 생성
        SecurityMember securityMember = new SecurityMember(mockMember);

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            securityMember,
            null,
            Collections.emptyList()  // 필요한 경우 권한 추가
        );

        // SecurityContext에 Authentication 설정
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // 테스트 종료 시 정리
    @PreDestroy
    public void cleanup() {
        SecurityContextHolder.clearContext();
    }

}
