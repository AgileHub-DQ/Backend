package dynamicquad.agilehub.issue.controller.response;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.domain.task.Task;
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
    private String type;
    private Long parentId;
    private AssigneeDto assignee;
    // TODO: 태그 추가 필요

    public static TaskResponse fromEntity(Task task, String projectKey, Long parentId) {
        return TaskResponse.builder()
            .id(task.getId())
            .title(task.getTitle())
            .key(projectKey + "-" + task.getNumber())
            .status(task.getStatus().toString())
            .type(IssueType.TASK.toString())
            .parentId(parentId)
            .assignee(
                task.getAssignee() == null ? new AssigneeDto() :
                    AssigneeDto.builder()
                        .id(task.getAssignee().getId())
                        .name(task.getAssignee().getName())
                        .build()
            )
            .build();
    }
    

}
