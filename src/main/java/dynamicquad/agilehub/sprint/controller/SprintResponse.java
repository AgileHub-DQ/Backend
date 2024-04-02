package dynamicquad.agilehub.sprint.controller;

import dynamicquad.agilehub.sprint.domain.Sprint;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class SprintResponse {

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class SprintCreateResponse {
        private Long sprintId;
        private String title;
        private String description;
        private String startDate;
        private String endDate;

        public static SprintCreateResponse fromEntity(Sprint sprint) {
            return SprintCreateResponse.builder()
                .sprintId(sprint.getId())
                .title(sprint.getTitle())
                .description(sprint.getTargetDescription())
                .startDate(sprint.getStartDate().toString())
                .endDate(sprint.getEndDate().toString())
                .build();
        }
    }
}
