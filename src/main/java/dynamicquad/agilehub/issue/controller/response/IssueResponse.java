package dynamicquad.agilehub.issue.controller.response;

import dynamicquad.agilehub.member.dto.AssigneeDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class IssueResponse {
    @Builder
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class IssueDto {
        private Long issueId;
        private String key;
        private String title;
        private String type;
        private String status;
        private String label;
        private String startDate;
        private String endDate;
        private ContentDto content;
        private AssigneeDto assignee;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class ContentDto {
        private String text;
        private List<String> imagesURLs;

        public ContentDto() {
            this.text = "";
            this.imagesURLs = List.of();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class IssueReadResponseDto {
        private IssueDto issue;
        private SubIssueDto parentIssue;
        private List<SubIssueDto> childIssues;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class SubIssueDto {
        private Long issueId;
        private String key;
        private String status;
        private String type;
        private String title;
        private AssigneeDto assignee;

        public SubIssueDto() {
            this.issueId = null;
            this.key = "";
            this.status = "";
            this.type = "";
            this.title = "";
            this.assignee = new AssigneeDto();
        }
    }


}
