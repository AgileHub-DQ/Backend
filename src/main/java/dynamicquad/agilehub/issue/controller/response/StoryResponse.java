package dynamicquad.agilehub.issue.controller.response;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.domain.story.Story;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class StoryResponse {
    private Long id;
    private String title;
    private String key;
    private String status;
    private String type;
    private String startDate;
    private String endDate;
    private Long parentId;
    private AssigneeDto assignee;
    // TODO: 태그 추가 필요

    public static StoryResponse fromEntity(Story story, String projectKey, Long parentId) {
        return StoryResponse.builder()
            .id(story.getId())
            .title(story.getTitle())
            .key(projectKey + "-" + story.getNumber())
            .status(story.getStatus().toString())
            .type(IssueType.STORY.toString())
            .startDate(story.getStartDate() == null ? "" : story.getStartDate().toString())
            .endDate(story.getEndDate() == null ? "" : story.getEndDate().toString())
            .parentId(parentId)
            .assignee(
                story.getAssignee() == null ? new AssigneeDto() :
                    AssigneeDto.builder()
                        .id(story.getAssignee().getId())
                        .name(story.getAssignee().getName())
                        .build()
            )
            .build();
    }
}
