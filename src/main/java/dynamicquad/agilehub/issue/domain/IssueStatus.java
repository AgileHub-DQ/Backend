package dynamicquad.agilehub.issue.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum IssueStatus {
    DO, PROGRESS, DONE;

    @JsonCreator
    public static IssueStatus parsing(String status) {
        return Stream.of(IssueStatus.values())
            .filter(st -> st.toString().equalsIgnoreCase(status))
            .findFirst()
            .orElse(null);
    }
}
