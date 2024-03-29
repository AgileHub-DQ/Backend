package dynamicquad.agilehub.issue.domain.task;


import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("TASK")
@Entity
public class Task extends Issue {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    private Story story;

    @Builder
    private Task(String title, String content, int number, IssueStatus status, Member assignee, Project project,
                 Story story) {
        super(title, content, number, status, assignee, project);
        this.story = story;
    }

    public void updateTask(Story story) {
        this.story = story;
    }
}
