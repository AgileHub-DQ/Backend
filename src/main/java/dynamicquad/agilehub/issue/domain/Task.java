package dynamicquad.agilehub.issue.domain;


import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
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
@DiscriminatorValue("TASK")
@Entity
public class Task extends Issue {

    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @Builder
    private Task(String title, String content, int number, IssueStatus status, IssueLabel label, Member assignee,
                 Project project, LocalDate startDate, LocalDate endDate, Story story) {
        super(title, content, number, status, label, assignee, project);
        this.story = story;
        this.startDate = startDate;
        this.endDate = endDate;
        if (story != null) {
            story.getTasks().add(this);
        }
    }

    public void updateTask(IssueRequestDto.EditIssue request, Member assignee, Story upStory) {
        super.updateIssue(request, assignee);
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();

        if (this.story != null) {
            this.story.getTasks().remove(this);
        }
        this.story = upStory;
        if (upStory != null) {
            upStory.getTasks().add(this);
        }

    }

    public static Task extractFromIssue(Issue issue) {
        if (!(issue instanceof Task task)) {
            throw new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND);
        }
        return task;
    }

    public void updatePeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
