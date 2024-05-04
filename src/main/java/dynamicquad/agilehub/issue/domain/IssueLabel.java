package dynamicquad.agilehub.issue.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum IssueLabel {
    NONE, PLAN, DESIGN, DEVELOP, TEST, FEEDBACK;

    @JsonCreator
    public static IssueLabel parsing(String value) {
        return Stream.of(IssueLabel.values())
            .filter(label -> label.toString().equalsIgnoreCase(value))
            .findFirst()
            .orElse(NONE);
    }

}
