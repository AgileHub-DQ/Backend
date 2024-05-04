package dynamicquad.agilehub.issue.domain.epic;

import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueEditRequest;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.member.domain.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("EPIC")
@Entity
@SuperBuilder
public class Epic extends Issue {

    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "epic", cascade = CascadeType.REMOVE)
    private List<Story> stories = new ArrayList<>();


    public void updateEpic(IssueEditRequest request, Member assignee) {
        super.updateIssue(request, assignee);
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
    }

}
