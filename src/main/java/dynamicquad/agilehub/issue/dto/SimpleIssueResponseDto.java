package dynamicquad.agilehub.issue.dto;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class SimpleIssueResponseDto {
    private SimpleIssueResponseDto() {
    }

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class ReadIssue {
        private Long id;
        private String title;
        private String type;
        private String key;
        private String status;
        private String label;
        private AssigneeDto assignee;

        public static ReadIssue from(Issue issue, String projectKey, IssueType issueType,
                                     AssigneeDto assignee) {
            return ReadIssue.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .type(issueType.toString())
                .status(String.valueOf(issue.getStatus()))
                .label(String.valueOf(issue.getLabel()))
                .assignee(assignee)
                .key(projectKey + "-" + issue.getNumber())
                .build();
        }

    }
}
