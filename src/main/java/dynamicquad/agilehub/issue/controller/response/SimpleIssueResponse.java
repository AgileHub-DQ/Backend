package dynamicquad.agilehub.issue.controller.response;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
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

    public static SimpleIssueResponse fromEntity(Issue issue, String projectKey, IssueType issueType) {
        return SimpleIssueResponse.builder()
            .id(issue.getId())
            .title(issue.getTitle())
            .type(issueType.toString())
            .key(projectKey + "-" + issue.getNumber())
            .build();
    }
}
