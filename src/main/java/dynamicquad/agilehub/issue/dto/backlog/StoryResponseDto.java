package dynamicquad.agilehub.issue.dto.backlog;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class StoryResponseDto {

    private StoryResponseDto() {
    }

    @Builder
    @Getter
    @EqualsAndHashCode
    public static class StoryDetailForBacklog {
        private Long id;
        private String title;
        private String key;
        private String status;
        private String label;
        private String type;
        private String startDate;
        private String endDate;
        private Long parentId;
        private AssigneeDto assignee;

        public static StoryDetailForBacklog from(Story story, String projectKey, Long parentId,
                                                 AssigneeDto assigneeDto) {
            return StoryDetailForBacklog.builder()
                .id(story.getId())
                .title(story.getTitle())
                .key(story.getNumber())
                .status(String.valueOf(story.getStatus()))
                .label(String.valueOf(story.getLabel()))
                .type(IssueType.STORY.toString())
                .startDate(story.getStartDate() == null ? "" : story.getStartDate().toString())
                .endDate(story.getEndDate() == null ? "" : story.getEndDate().toString())
                .parentId(parentId)
                .assignee(assigneeDto)
                .build();
        }
    }
}
