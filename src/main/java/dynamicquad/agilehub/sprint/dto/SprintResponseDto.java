package dynamicquad.agilehub.sprint.dto;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
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
            if (issue.getIssueType().equals(IssueType.EPIC)) {
                return new IssueDetailInSprint();
            }

            return IssueDetailInSprint.builder()
                .title(issue.getTitle())
                .type(issue.getIssueType().toString())
                .issueId(issue.getId())
                .key(issue.getNumber())
                .status(String.valueOf(issue.getStatus()))
                .label(String.valueOf(issue.getLabel()))
                .startDate(issue.getStartDate() != null ? issue.getStartDate().toString() : "")
                .endDate(issue.getEndDate() != null ? issue.getEndDate().toString() : "")
                .assigneeDto(assigneeDto)
                .build();
        }

    }

}
