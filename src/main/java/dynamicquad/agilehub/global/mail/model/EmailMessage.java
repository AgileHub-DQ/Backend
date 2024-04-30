package dynamicquad.agilehub.global.mail.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailMessage {

    private final String to;          // 수신자
    private final String subject;     // 제목
    private final String message;     // 내용

    @Builder
    public EmailMessage(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }
}
