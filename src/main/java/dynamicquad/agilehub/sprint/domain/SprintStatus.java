package dynamicquad.agilehub.sprint.domain;

public enum SprintStatus {

    PLANNED("진행전"),
    ACTIVE("진행중"),
    COMPLETED("완료됨");

    private final String description;

    SprintStatus(String description) {
        this.description = description;
    }
}
