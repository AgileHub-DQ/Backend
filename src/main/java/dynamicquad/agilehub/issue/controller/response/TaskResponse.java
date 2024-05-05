package dynamicquad.agilehub.issue.controller.response;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class TaskResponse {
    private Long id;
    private String title;
    private String key;
    private String status;
    private String label;
    private String type;
    private Long parentId;
    private AssigneeDto assignee;

    public static TaskResponse fromEntity(Task task, String projectKey, Long parentId, AssigneeDto assigneeDto) {
        return TaskResponse.builder()
            .id(task.getId())
            .title(task.getTitle())
            .key(projectKey + "-" + task.getNumber())
            .status(task.getStatus().toString())
            .type(IssueType.TASK.toString())
            .parentId(parentId)
            .assignee(assigneeDto)
            .build();
    }


}
