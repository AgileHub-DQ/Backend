package dynamicquad.agilehub.email;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AmazonSESServiceTest {
    @Autowired
    private AmazonSESService amazonSESService;

    @Test
    @DisplayName("Amazon SES를 이용한 이메일 발송 테스트")
    void sendEmailTest() {
        // given
        String subject = "테스트 이메일 발송";
        String to = "evobusiness99@gmail.com";
        Map<String, Object> variables = Map.of("projectName", "테스트 프로젝트", "inviteCode", "123456");

        amazonSESService.sendEmail(subject, variables, to);
    }
}