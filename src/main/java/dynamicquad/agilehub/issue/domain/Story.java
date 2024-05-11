package dynamicquad.agilehub.issue.domain;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
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

    public void updateStory(IssueRequestDto.EditIssue request, Member assignee, Epic upEpic) {
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

    public static Story extractFromIssue(Issue issue) {
        if (!(issue instanceof Story story)) {
            throw new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND);
        }
        return story;
    }

}
