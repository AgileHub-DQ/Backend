package dynamicquad.agilehub.issue.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum IssueStatus {
    DO, PROGRESS, DONE;

    @JsonCreator
    public static IssueStatus parsing(String value) {
        return Stream.of(IssueStatus.values())
            .filter(status -> status.toString().equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }
}
