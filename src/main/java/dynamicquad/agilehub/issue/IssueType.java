package dynamicquad.agilehub.issue;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
