package dynamicquad.agilehub.issue.controller.response;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class IssueHierarchyResponse {
    private Long id;
    private String title;
    private String key;
    private String status;
    private String type;
    private String startDate;
    private String endDate;
    private Long parentId;
    private List<IssueHierarchyResponse> children;

    public void addChild(IssueHierarchyResponse child) {
        if (children == null) {
            children = new ArrayList<>();
        }

        children.add(child);
    }

    public static IssueHierarchyResponse fromEntity(Issue issue, String projectKey, Long parentId) {
        if (issue instanceof Epic epic) {
            return IssueHierarchyResponse.builder()
                .id(epic.getId())
                .title(epic.getTitle())
                .key(projectKey + "-" + epic.getNumber())
                .status(epic.getStatus().toString())
                .type(IssueType.EPIC.toString())
                .startDate(epic.getStartDate() == null ? "" : epic.getStartDate().toString())
                .endDate(epic.getEndDate() == null ? "" : epic.getEndDate().toString())
                .parentId(parentId)
                .build();
        } else if (issue instanceof Story story) {
            return IssueHierarchyResponse.builder()
                .id(story.getId())
                .title(story.getTitle())
                .key(projectKey + "-" + story.getNumber())
                .status(story.getStatus().toString())
                .type(IssueType.STORY.toString())
                .startDate(story.getStartDate() == null ? "" : story.getStartDate().toString())
                .endDate(story.getEndDate() == null ? "" : story.getEndDate().toString())
                .parentId(parentId)
                .build();
        } else if (issue instanceof Task task) {
            return IssueHierarchyResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .key(projectKey + "-" + task.getNumber())
                .status(task.getStatus().toString())
                .type(IssueType.TASK.toString())
                .startDate("")
                .endDate("")
                .parentId(parentId)
                .build();
        }
        throw new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND);
    }
}
