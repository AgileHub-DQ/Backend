package dynamicquad.agilehub.project.model;

import dynamicquad.agilehub.global.mail.model.EmailInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class InviteEmailInfo extends EmailInfo {

    private final String projectName;
    private final String inviteCode;

    public InviteEmailInfo(String to, String subject, String message, String projectName, String inviteCode) {
        super(to, subject, message);
        this.projectName = projectName;
        this.inviteCode = inviteCode;
    }
}
