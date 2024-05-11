package dynamicquad.agilehub.issue.dto;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Image;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public class IssueResponseDto {
    private IssueResponseDto() {
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class IssueAndSubIssueDetail {
        private IssueDetail issue;
        private SubIssueDetail parentIssue;
        private List<SubIssueDetail> childIssues;

        public static IssueAndSubIssueDetail from(IssueDetail issueDetail, SubIssueDetail parentIssue,
                                                  List<SubIssueDetail> childIssues) {
            return IssueAndSubIssueDetail.builder()
                .issue(issueDetail)
                .parentIssue(parentIssue)
                .childIssues(childIssues)
                .build();
        }
    }


    @Builder
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class IssueDetail {
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

        public static IssueDetail from(Issue issue, ContentDto contentDto, AssigneeDto assigneeDto,
                                       IssueType issueType) {
            IssueDetail issueDetail = IssueDetail.builder()
                .issueId(issue.getId())
                .key(issue.getProject().getKey() + "-" + issue.getNumber())
                .title(issue.getTitle())
                .type(issueType.toString())
                .status(String.valueOf(issue.getStatus()))
                .label(String.valueOf(issue.getLabel()))
                .content(contentDto)
                .assignee(assigneeDto)
                .build();

            if (IssueType.EPIC.equals(issueType)) {
                Epic epic = Epic.extractFromIssue(issue);
                issueDetail.startDate = epic.getStartDate() == null ? "" : epic.getStartDate().toString();
                issueDetail.endDate = epic.getEndDate() == null ? "" : epic.getEndDate().toString();
            }

            if (IssueType.STORY.equals(issueType)) {
                Story story = Story.extractFromIssue(issue);
                issueDetail.startDate = story.getStartDate() == null ? "" : story.getStartDate().toString();
                issueDetail.endDate = story.getEndDate() == null ? "" : story.getEndDate().toString();
            }

            return issueDetail;
        }
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

        public static ContentDto from(Issue issue) {
            return ContentDto.builder()
                .text(issue.getContent())
                .imagesURLs(issue.getImages().stream().map(Image::getPath).toList())
                .build();
        }
    }

    @Builder
    @Getter
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class SubIssueDetail {
        private Long issueId;
        private String key;
        private String status;
        private String label;
        private String type;
        private String title;
        private AssigneeDto assignee;

        public SubIssueDetail() {
            this.issueId = null;
            this.key = "";
            this.status = "";
            this.type = "";
            this.title = "";
            this.assignee = new AssigneeDto();
        }

        public static SubIssueDetail from(Issue issue, IssueType issueType, AssigneeDto assigneeDto) {
            return SubIssueDetail.builder()
                .issueId(issue.getId())
                .key(issue.getProject().getKey() + "-" + issue.getNumber())
                .status(String.valueOf(issue.getStatus()))
                .label(String.valueOf(issue.getLabel()))
                .type(issueType.toString())
                .title(issue.getTitle())
                .assignee(assigneeDto)
                .build();
        }

    }


}
