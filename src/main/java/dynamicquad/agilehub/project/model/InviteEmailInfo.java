package dynamicquad.agilehub.project.model;

import dynamicquad.agilehub.global.mail.model.EmailInfo;
import lombok.Builder;
import lombok.Getter;

@Getter
public class InviteEmailInfo extends EmailInfo {

    private final String projectName;
    private final String inviteCode;

    @Builder
    public InviteEmailInfo(String to, String subject, String message, String projectName, String inviteCode) {
        super(to, subject, message);
        this.projectName = projectName;
        this.inviteCode = inviteCode;
    }
}
