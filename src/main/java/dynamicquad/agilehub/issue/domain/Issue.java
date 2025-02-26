package dynamicquad.agilehub.issue.domain;

import dynamicquad.agilehub.comment.domain.Comment;
import dynamicquad.agilehub.global.domain.BaseEntity;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.dto.IssueRequestDto.CreateIssue;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.sprint.domain.Sprint;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Table(name = "issue_new")  // 기존 테이블(issue) 대신 issue_new 사용
@Entity
@Getter
public class Issue extends BaseEntity {

    protected Issue() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long id;

    private String title;
    private String content;
    private String number;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    private IssueLabel label;

    @Enumerated(EnumType.STRING)
    private IssueType issueType;  // Epic, Story, Task 구분을 위한 필드 추가

    private LocalDate startDate;  // Epic, Story, Task 공통 사용
    private LocalDate endDate;

    private Integer storyPoint;  // Story 전용 필드
    private Long parentIssueId;  // 계층 구조 (Epic → Story → Task) 표현

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @OneToMany(mappedBy = "issue", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "issue", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Comment> comments = new ArrayList<>();

    public void setSprint(Sprint newSprint) {
        this.sprint = newSprint;
    }

    protected Issue(String title, String content, String number, IssueStatus status, IssueLabel label, Member assignee,
                    Project project, IssueType issueType, LocalDate startDate, LocalDate endDate, Integer storyPoint,
                    Long parentIssueId) {
        this.title = title;
        this.content = content;
        this.number = number;
        this.status = status;
        this.label = label;
        this.assignee = assignee;
        this.project = project;
        this.issueType = issueType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.storyPoint = storyPoint;
        this.parentIssueId = parentIssueId;
    }

    public static Issue createEpic(CreateIssue request, Member assignee, String issueNumber, Project project) {
        return new Issue(request.getTitle(), request.getContent(), issueNumber, request.getStatus(), request.getLabel(),
            assignee, project, IssueType.EPIC, request.getStartDate(), request.getEndDate(), null, null);
    }

    public static Issue createStory(CreateIssue request, Member assignee, String issueNumber, Project project) {
        return new Issue(request.getTitle(), request.getContent(), issueNumber, request.getStatus(), request.getLabel(),
            assignee, project, IssueType.STORY, request.getStartDate(), request.getEndDate(), null,
            request.getParentId());
    }

    public static Issue createTask(CreateIssue request, Member assignee, String issueNumber, Project project) {
        return new Issue(request.getTitle(), request.getContent(), issueNumber, request.getStatus(), request.getLabel(),
            assignee, project, IssueType.TASK, request.getStartDate(), request.getEndDate(), null,
            request.getParentId());
    }

    public void updateIssue(IssueRequestDto.EditIssue request, Member assignee) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.status = request.getStatus();
        this.label = request.getLabel();
        this.assignee = assignee;

        if (this.issueType == IssueType.EPIC || this.issueType == IssueType.STORY) {
            this.startDate = request.getStartDate();
            this.endDate = request.getEndDate();
        }

    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateStatus(IssueStatus updateStatus) {
        this.status = updateStatus;
    }

    public void updatePeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}