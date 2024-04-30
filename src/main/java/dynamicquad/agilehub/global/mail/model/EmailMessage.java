package dynamicquad.agilehub.global.mail.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailMessage {

    private String to;          // 수신자
    private String subject;     // 제목
    private String message;     // 내용

    private String inviteCode;  // 초대코드

    @Builder
    public EmailMessage(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    @Builder
    public EmailMessage(String to, String subject, String message, String inviteCode) {
        this.to = to;
        this.subject = subject;
        this.message = message;
        this.inviteCode = inviteCode;
    }
}
