package dynamicquad.agilehub.issue.domain.story;

import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueEditRequest;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueLabel;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("STORY")
@Entity
public class Story extends Issue {

    private Integer storyPoint;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id")
    private Epic epic;

    @OneToMany(mappedBy = "story", cascade = CascadeType.REMOVE)
    private List<Task> tasks = new ArrayList<>();

    @Builder
    private Story(String title, String content, int number, IssueStatus status, IssueLabel label, Member assignee,
                  Project project,
                  int storyPoint, LocalDate startDate, LocalDate endDate, Epic epic) {
        super(title, content, number, status, label, assignee, project);
        this.storyPoint = storyPoint;
        this.startDate = startDate;
        this.endDate = endDate;
        this.epic = epic;
        if (epic != null) {
            epic.getStories().add(this);
        }
    }

    public void updateStory(IssueEditRequest request, Member assignee, Epic upEpic) {
        super.updateIssue(request, assignee);
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();

        if (this.epic != null) {
            this.epic.getStories().remove(this);
        }
        this.epic = upEpic;
        if (upEpic != null) {
            upEpic.getStories().add(this);
        }
    }

}
