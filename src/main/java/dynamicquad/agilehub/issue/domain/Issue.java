package dynamicquad.agilehub.issue.domain;

import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueEditRequest;
import dynamicquad.agilehub.issue.domain.image.Image;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.sprint.domain.Sprint;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DiscriminatorColumn(name = "issue_type")
@Table(name = "issue")
@Entity
public abstract class Issue {

    @Id
    @Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "issue_id")
    private Long id;

    private String title;

    private String content;

    private int number;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

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

    public void setSprint(Sprint newSprint) {
        this.sprint = newSprint;
    }

    protected Issue(String title, String content, int number, IssueStatus status, Member assignee, Project project) {
        this.title = title;
        this.content = content;
        this.number = number;
        this.status = status;
        this.assignee = assignee;
        this.project = project;

    }

    protected void updateIssue(IssueEditRequest request, Member assignee) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.status = request.getStatus();
        this.assignee = assignee;
    }


}
