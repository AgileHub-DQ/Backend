package dynamicquad.agilehub.issue.dto.backlog;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class TaskResponseDto {

    private TaskResponseDto() {
    }

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class TaskDetailForBacklog {
        private Long id;
        private String title;
        private String key;
        private String status;
        private String label;
        private String type;
        private Long parentId;
        private AssigneeDto assignee;

        public static TaskDetailForBacklog from(Task task, String projectKey, Long parentId,
                                                AssigneeDto assigneeDto) {
            return TaskDetailForBacklog.builder()
                .id(task.getId())
                .title(task.getTitle())
                .key(projectKey + "-" + task.getNumber())
                .status(String.valueOf(task.getStatus()))
                .label(String.valueOf(task.getLabel()))
                .type(IssueType.TASK.toString())
                .parentId(parentId)
                .assignee(assigneeDto)
                .build();
        }
    }
}
