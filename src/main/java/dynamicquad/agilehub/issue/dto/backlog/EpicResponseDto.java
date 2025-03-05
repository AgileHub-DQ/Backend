package dynamicquad.agilehub.issue.dto.backlog;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public class EpicResponseDto {
    private EpicResponseDto() {
    }

    @Builder
    @Getter
    @EqualsAndHashCode
    @ToString
    public static class EpicDetailForBacklog {
        private Long id;
        private String title;
        private String key;
        private String status;
        private String type;
        private String label;
        private String startDate;
        private String endDate;
        private AssigneeDto assignee;

        public static EpicDetailForBacklog from(Issue epic, String key, AssigneeDto assignee) {
            return EpicDetailForBacklog.builder()
                .id(epic.getId())
                .title(epic.getTitle())
                .key(epic.getNumber())
                .status(epic.getStatus().toString())
                .label(String.valueOf(epic.getLabel()))
                .type(IssueType.EPIC.toString())
                .startDate(epic.getStartDate() == null ? "" : epic.getStartDate().toString())
                .endDate(epic.getEndDate() == null ? "" : epic.getEndDate().toString())
                .assignee(assignee)
                .build();
        }
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    @ToString
    public static class EpicDetailWithStatistic {
        private EpicDetailForBacklog issue;
        private EpicStatistic statistic;

        public EpicDetailWithStatistic(EpicDetailForBacklog epicDetail, EpicStatistic statisticDto) {
            this.issue = epicDetail;
            this.statistic = statisticDto;
        }
    }

    public interface EpicStatistic {
        Long getEpicId();

        Long getStoriesCount();

        Long getStatusDo();

        Long getStatusProgress();

        Long getStatusDone();
    }
}
