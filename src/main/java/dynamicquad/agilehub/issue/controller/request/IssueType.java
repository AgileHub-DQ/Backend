package dynamicquad.agilehub.issue.controller.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum IssueType {
    EPIC, STORY, TASK;

    @JsonCreator
    public static IssueType parsing(String value) {
        return Stream.of(IssueType.values())
            .filter(status -> status.toString().equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }
}
