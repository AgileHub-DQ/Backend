package dynamicquad.agilehub.report;

import dynamicquad.agilehub.global.mail.model.EmailInfo;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SummaryEmailInfo extends EmailInfo {
    private final String projectName;
    private final String report;

    public SummaryEmailInfo(String to, String subject, String message, String projectName, String report) {
        super(to, subject, message);
        this.projectName = projectName;
        this.report = report;
    }
}
