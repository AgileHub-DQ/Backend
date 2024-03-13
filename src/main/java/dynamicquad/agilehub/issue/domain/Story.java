package dynamicquad.agilehub.issue.domain;

import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("STORY")
@Entity
public class Story extends Issue {

    private int storyPoint;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "epic_id")
    private Epic epic;

    @Builder
    public Story(String title, String content, int number, IssueStatus status, Member assignee, Project project,
                 int storyPoint, LocalDate startDate, LocalDate endDate, Epic epic) {
        super(title, content, number, status, assignee, project);
        this.storyPoint = storyPoint;
        this.startDate = startDate;
        this.endDate = endDate;
        this.epic = epic;
    }
}
