package dynamicquad.agilehub.sprint.controller.response;

import dynamicquad.agilehub.issue.controller.response.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.sprint.domain.Sprint;
import java.util.List;
import lombok.AllArgsConstructor;
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
                .startDate(sprint.getStartDate() == null ? "" : sprint.getStartDate().toString())
                .endDate(sprint.getEndDate() == null ? "" : sprint.getEndDate().toString())
                .build();
        }
    }

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class SprintReadResponse {
        private Long sprintId;
        private String title;
        private String description;
        private String startDate;
        private String endDate;
        private Long issueCount;
        private List<IssueInSprintDto> issues;
    }

    @Builder
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class IssueInSprintDto {

        private String title;
        private String type;
        private Long issueId;
        private String key;
        private String status;
        private String startDate;
        private String endDate;
        private AssigneeDto assigneeDto;

        public IssueInSprintDto() {
            this.title = "";
            this.type = "";
            this.issueId = null;
            this.key = "";
            this.status = "";
            this.startDate = "";
            this.endDate = "";
            this.assigneeDto = new AssigneeDto();
        }


        public static IssueInSprintDto fromEntity(Issue issue, String key, AssigneeDto assigneeDto) {
            if (issue instanceof Epic) {
                return new IssueInSprintDto();
            }

            String type = issue instanceof Story ? "STORY" : "TASK";
            String startDate =
                issue instanceof Story && ((Story) issue).getStartDate() != null ? ((Story) issue).getStartDate()
                    .toString() : "";
            String endDate =
                issue instanceof Story && ((Story) issue).getEndDate() != null ? ((Story) issue).getEndDate().toString()
                    : "";

            return IssueInSprintDto.builder()
                .title(issue.getTitle())
                .type(type)
                .issueId(issue.getId())
                .key(key + "-" + issue.getNumber())
                .status(String.valueOf(issue.getStatus()))
                .startDate(startDate)
                .endDate(endDate)
                .assigneeDto(assigneeDto)
                .build();
        }

    }

}
