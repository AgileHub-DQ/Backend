package dynamicquad.agilehub.member;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.member.domain.Member;
import org.junit.jupiter.api.Test;

class MemberTest {

    @Test
    void 멤버를_생성한다() {
        Member member = Member.builder()
            .email("sdfadf@naver.com")
            .profileImageUrl("asdf")
            .name("adfa")
            .build();

        assertThat(member.getEmail()).isEqualTo("sdfadf@naver.com");
    }

}