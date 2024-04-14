package dynamicquad.agilehub.issue.controller.response;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class EpicResponse {
    private Long id;
    private String title;
    private String key;
    private String status;
    private String type;
    private String startDate;
    private String endDate;
    private AssigneeDto assignee;
    //TODO: 태그 추가 필요

    public static EpicResponse fromEntity(Epic epic, String projectKey) {
        return EpicResponse.builder()
            .id(epic.getId())
            .title(epic.getTitle())
            .key(projectKey + "-" + epic.getNumber())
            .status(epic.getStatus().toString())
            .type(IssueType.EPIC.toString())
            .startDate(epic.getStartDate() == null ? "" : epic.getStartDate().toString())
            .endDate(epic.getEndDate() == null ? "" : epic.getEndDate().toString())
            .assignee(
                epic.getAssignee() == null ? new AssigneeDto() :
                    AssigneeDto.builder()
                        .id(epic.getAssignee().getId())
                        .name(epic.getAssignee().getName())
                        .build()
            )
            .build();
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    public static class EpicWithStatisticResponse {
        private EpicResponse issue;
        private EpicStatisticDto statistic;

        public EpicWithStatisticResponse(EpicResponse epicResponse, EpicStatisticDto statisticDto) {
            this.issue = epicResponse;
            this.statistic = statisticDto;
        }
    }

    @Data
    public static class EpicStatisticDto {
        private Long epicId;
        private Long statusDone;
        private Long statusProgress;
        private Long statusDo;
        private Long storiesCount;
    }

}
