package dynamicquad.agilehub.global.mail.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class InviteEmailMessage extends EmailMessage {

    private final String inviteCode;

    @Builder
    public InviteEmailMessage(String to, String subject, String message, String inviteCode) {
        super(to, subject, message);
        this.inviteCode = inviteCode;
    }
}
