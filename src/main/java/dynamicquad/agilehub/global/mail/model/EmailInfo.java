package dynamicquad.agilehub.global.mail.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class EmailInfo {

    private final String to;          // 수신자
    private final String subject;     // 제목
    private final String message;     // 내용

    public EmailInfo(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }
}
