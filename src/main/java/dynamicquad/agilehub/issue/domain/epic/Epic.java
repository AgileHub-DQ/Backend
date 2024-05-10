package dynamicquad.agilehub.issue.domain.epic;

import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueLabel;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
@DiscriminatorValue("EPIC")
@Entity
public class Epic extends Issue {

    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "epic", cascade = CascadeType.REMOVE)
    private List<Story> stories = new ArrayList<>();

    @Builder
    private Epic(String title, String content, int number, IssueStatus status, IssueLabel label, Member assignee,
                 Project project, LocalDate startDate, LocalDate endDate) {
        super(title, content, number, status, label, assignee, project);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void updateEpic(IssueRequestDto.EditIssue request, Member assignee) {
        super.updateIssue(request, assignee);
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
    }

}
