package dynamicquad.agilehub.global.mail.dto;

import lombok.Builder;
import lombok.Getter;

public class MailRequestDto {

    private MailRequestDto() {
    }

    @Getter
    @Builder
    public static class SendInviteMail {
        String email;
        long projectId;
    }
}
