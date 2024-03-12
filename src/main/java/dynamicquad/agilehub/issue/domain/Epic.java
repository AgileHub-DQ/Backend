package dynamicquad.agilehub.issue.domain;

import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("Epic")
@Entity
public class Epic extends Issue {

    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public Epic(String title, String content, int number, IssueStatus status, Member assignee, Project project,
                LocalDate startDate, LocalDate endDate) {
        super(title, content, number, status, assignee, project);
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
