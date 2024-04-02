package dynamicquad.agilehub.sprint.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum SprintStatus {
    PLANNED,
    ACTIVE,
    COMPLETED;

    @JsonCreator
    public static SprintStatus parsing(String value) {
        return Stream.of(SprintStatus.values())
            .filter(status -> status.toString().equalsIgnoreCase(value))
            .findFirst()
            .orElse(null);
    }
}
