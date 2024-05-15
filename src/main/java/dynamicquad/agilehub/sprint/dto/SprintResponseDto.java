package dynamicquad.agilehub.sprint.dto;

import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.issue.domain.Task;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.sprint.domain.Sprint;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class SprintResponseDto {
    private SprintResponseDto() {
    }

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class CreatedSprint {
        private Long sprintId;
        private String title;
        private String description;
        private String startDate;
        private String endDate;

        public static CreatedSprint from(Sprint sprint) {
            return CreatedSprint.builder()
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
    public static class SprintDetail {
        private Long sprintId;
        private String title;
        private String status;
        private String description;
        private String startDate;
        private String endDate;
        private Long issueCount;
        private List<IssueDetailInSprint> issues;

        public static SprintDetail from(Sprint sprint, List<IssueDetailInSprint> issues) {
            return SprintDetail.builder()
                .sprintId(sprint.getId())
                .title(sprint.getTitle())
                .status(String.valueOf(sprint.getStatus()))
                .description(sprint.getTargetDescription())
                .startDate(sprint.getStartDate() == null ? "" : sprint.getStartDate().toString())
                .endDate(sprint.getEndDate() == null ? "" : sprint.getEndDate().toString())
                .issueCount((long) issues.size())
                .issues(issues)
                .build();
        }
    }


    @Builder
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class IssueDetailInSprint {

        private String title;
        private String type;
        private Long issueId;
        private String key;
        private String status;
        private String label;
        private String startDate;
        private String endDate;
        private AssigneeDto assigneeDto;

        public IssueDetailInSprint() {
            this.title = "";
            this.type = "";
            this.issueId = null;
            this.key = "";
            this.status = "";
            this.label = "";
            this.startDate = "";
            this.endDate = "";
            this.assigneeDto = new AssigneeDto();
        }


        public static IssueDetailInSprint from(Issue issue, String key, AssigneeDto assigneeDto) {
            if (issue instanceof Epic) {
                return new IssueDetailInSprint();
            } else if (issue instanceof Story story) {
                return IssueDetailInSprint.builder()
                    .title(story.getTitle())
                    .type("STORY")
                    .issueId(story.getId())
                    .key(key + "-" + story.getNumber())
                    .status(String.valueOf(story.getStatus()))
                    .label(String.valueOf(story.getLabel()))
                    .startDate(story.getStartDate() != null ? story.getStartDate().toString() : "")
                    .endDate(story.getEndDate() != null ? story.getEndDate().toString() : "")
                    .assigneeDto(assigneeDto)
                    .build();
            } else if (issue instanceof Task task) {
                return IssueDetailInSprint.builder()
                    .title(task.getTitle())
                    .type("TASK")
                    .issueId(task.getId())
                    .key(key + "-" + task.getNumber())
                    .status(String.valueOf(task.getStatus()))
                    .label(String.valueOf(task.getLabel()))
                    .startDate(task.getStartDate() != null ? task.getStartDate().toString() : "")
                    .endDate(task.getEndDate() != null ? task.getEndDate().toString() : "")
                    .assigneeDto(assigneeDto)
                    .build();
            }

            return new IssueDetailInSprint();
        }

    }

}
