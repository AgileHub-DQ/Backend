package dynamicquad.agilehub.issue.domain.story;

import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
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

    @Builder
    private Story(String title, String content, int number, IssueStatus status, Member assignee, Project project,
                  int storyPoint, LocalDate startDate, LocalDate endDate, Epic epic) {
        super(title, content, number, status, assignee, project);
        this.storyPoint = storyPoint;
        this.startDate = startDate;
        this.endDate = endDate;
        this.epic = epic;
    }

    public void updateStory(int storyPoint, LocalDate startDate, LocalDate endDate, Epic epic) {
        this.storyPoint = storyPoint;
        this.startDate = startDate;
        this.endDate = endDate;
        this.epic = epic;
    }


}
