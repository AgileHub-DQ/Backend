package dynamicquad.agilehub.issue.controller.response;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import lombok.Builder;
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
    private String label;
    private String startDate;
    private String endDate;
    private AssigneeDto assignee;


    public static EpicResponse fromEntity(Epic epic, String projectKey, AssigneeDto assigneeDto) {
        return EpicResponse.builder()
            .id(epic.getId())
            .title(epic.getTitle())
            .key(projectKey + "-" + epic.getNumber())
            .status(epic.getStatus().toString())
            .label(epic.getLabel().toString())
            .type(IssueType.EPIC.toString())
            .startDate(epic.getStartDate() == null ? "" : epic.getStartDate().toString())
            .endDate(epic.getEndDate() == null ? "" : epic.getEndDate().toString())
            .assignee(assigneeDto)
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


    public interface EpicStatisticDto {
        Long getEpicId();

        Long getStoriesCount();

        Long getStatusDo();

        Long getStatusProgress();

        Long getStatusDone();

    }
}
