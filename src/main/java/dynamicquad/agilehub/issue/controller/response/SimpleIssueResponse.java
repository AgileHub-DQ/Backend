package dynamicquad.agilehub.issue.controller.response;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class SimpleIssueResponse {
    private Long id;
    private String title;
    private String type;
    private String key;
    private String status;
    private String label;
    private AssigneeDto assignee;

    public static SimpleIssueResponse fromEntity(Issue issue, String projectKey, IssueType issueType,
                                                 AssigneeDto assignee) {
        return SimpleIssueResponse.builder()
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
