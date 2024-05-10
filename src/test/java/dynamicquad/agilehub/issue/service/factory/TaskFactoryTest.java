package dynamicquad.agilehub.issue.service.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class TaskFactoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private TaskFactory taskFactory;

    @Test
    @Transactional
    void 부모이슈가_에픽이거나_테스크일때_예외처리() {
        // given
        Project project = createProject("프로젝트1", "project124151");
        em.persist(project);

        Epic epic = Epic.builder()
            .title("에픽제목")
            .status(IssueStatus.DONE)
            .build();
        em.persist(epic);

        Task task = Task.builder()
            .title("task제목")
            .status(IssueStatus.DONE)
            .build();
        em.persist(task);

        Long parentIssueId1 = epic.getId();
        Long parentIssueId2 = task.getId();

        // when
        IssueRequestDto.CreateIssue taskIssueRequest = IssueRequestDto.CreateIssue.builder()
            .title("테스크 제목")
            .type(IssueType.TASK)
            .status(IssueStatus.DONE)
            .parentId(parentIssueId1)
            .build();

        // then
        assertThatThrownBy(() -> taskFactory.createIssue(taskIssueRequest, project))
            .isInstanceOf(GeneralException.class)
            .hasFieldOrPropertyWithValue("status", ErrorStatus.PARENT_ISSUE_NOT_STORY);

        // when
        IssueRequestDto.CreateIssue taskIssueRequest1 = IssueRequestDto.CreateIssue.builder()
            .title("테스크 제목")
            .type(IssueType.TASK)
            .status(IssueStatus.DONE)
            .parentId(parentIssueId2)
            .build();

        // then
        assertThatThrownBy(() -> taskFactory.createIssue(taskIssueRequest1, project))
            .isInstanceOf(GeneralException.class)
            .hasFieldOrPropertyWithValue("status", ErrorStatus.PARENT_ISSUE_NOT_STORY);
    }

    @Test
    @Transactional
    void 부모이슈를_정상적으로_등록() {
        // given
        Project project = createProject("프로젝트1", "proje123ct1");
        em.persist(project);

        Story story = Story.builder()
            .title("story제목")
            .status(IssueStatus.DONE)
            .build();
        em.persist(story);

        Long parentIssueId = story.getId();

        // when
        IssueRequestDto.CreateIssue taskIssueRequest = IssueRequestDto.CreateIssue.builder()
            .title("테스크 제목")
            .type(IssueType.TASK)
            .status(IssueStatus.DONE)
            .parentId(parentIssueId)
            .build();

        Long issueId = taskFactory.createIssue(taskIssueRequest, project);

        // then
        Task task = em.find(Task.class, issueId);
        assertThat(task.getStory().getId()).isEqualTo(story.getId());
        assertThat(task.getStory().getTitle()).isEqualTo(story.getTitle());


    }

    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }
}