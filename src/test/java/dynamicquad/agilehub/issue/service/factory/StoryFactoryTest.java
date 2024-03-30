package dynamicquad.agilehub.issue.service.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class StoryFactoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private StoryFactory storyFactory;


    @Test
    @Transactional
    void 부모이슈가_스토리나_테스크일때_예외처리() {
        // given
        Project project = createProject("프로젝트1", "project1");
        em.persist(project);

        Story story = Story.builder()
            .title("story제목")
            .status(IssueStatus.DONE)
            .build();

        em.persist(story);

        Task task = Task.builder()
            .title("task제목")
            .status(IssueStatus.DONE)
            .build();
        em.persist(task);

        Long parentIssueId1 = story.getId();
        Long parentIssueId2 = task.getId();

        // when
        IssueCreateRequest storyIssueRequest = IssueCreateRequest.builder()
            .title("스토리 제목")
            .type(IssueType.STORY)
            .status(IssueStatus.DONE)
            .parentId(parentIssueId1)
            .build();

        // then
        assertThatThrownBy(() -> storyFactory.createIssue(storyIssueRequest, project))
            .isInstanceOf(GeneralException.class)
            .hasFieldOrPropertyWithValue("status", ErrorStatus.PARENT_ISSUE_NOT_EPIC);

        // when
        IssueCreateRequest storyIssueRequest2 = IssueCreateRequest.builder()
            .title("스토리 제목")
            .type(IssueType.STORY)
            .status(IssueStatus.DONE)
            .parentId(parentIssueId2)
            .build();

        assertThatThrownBy(() -> storyFactory.createIssue(storyIssueRequest2, project))
            .hasFieldOrPropertyWithValue("status", ErrorStatus.PARENT_ISSUE_NOT_EPIC);


    }

    @Test
    @Transactional
    void 부모에픽을_정상적으로_찾는다() {
        // given
        Project project = createProject("프로젝트1", "pro1231ject1");
        em.persist(project);
        Epic epic = Epic.builder()
            .title("에픽 제목")
            .status(IssueStatus.DO)
            .build();
        em.persist(epic);

        // when
        IssueCreateRequest storyIssueRequest = IssueCreateRequest.builder()
            .title("스토리 제목")
            .type(IssueType.STORY)
            .status(IssueStatus.DO)
            .parentId(epic.getId())
            .build();

        // then
        assertThat(storyFactory.retrieveEpicFromParentIssue(epic.getId())).isEqualTo(epic);

    }

    @Test
    @Transactional
    void 부모이슈가_정상적으로_등록() {
        // given
        Project project = createProject("프로젝트1", "project12451");
        em.persist(project);
        Epic epic = Epic.builder()
            .title("에픽 제목")
            .status(IssueStatus.DO)
            .build();
        em.persist(epic);

        // when
        IssueCreateRequest storyIssueRequest = IssueCreateRequest.builder()
            .title("스토리 제목")
            .type(IssueType.STORY)
            .status(IssueStatus.DO)
            .parentId(epic.getId())
            .build();

        Long issueId = storyFactory.createIssue(storyIssueRequest, project);

        // then
        Story story = em.find(Story.class, issueId);
        assertThat(story.getEpic().getTitle()).isEqualTo("에픽 제목");


    }

    @Test
    @Transactional
    void 하위_이슈들_정상적으로_가져오기() {
        // given
        Project project = createProject("프로젝트1", "project12411");
        em.persist(project);
        Epic epic = Epic.builder()
            .title("에픽 제목")
            .project(project)
            .status(IssueStatus.DO)
            .build();
        em.persist(epic);

        Story story = Story.builder()
            .project(project)
            .title("스토리 제목")
            .status(IssueStatus.DO)
            .epic(epic)
            .build();
        em.persist(story);

        Task task1 = Task.builder()
            .project(project)
            .title("task1")
            .status(IssueStatus.DO)
            .story(story)
            .build();
        em.persist(task1);

        Task task2 = Task.builder()
            .project(project)
            .title("task2")
            .status(IssueStatus.DO)
            .story(story)
            .build();
        em.persist(task2);

        // when
        Story storyFromDB = em.find(Story.class, story.getId());
        List<SubIssueDto> childIssueDtos = storyFactory.createChildIssueDtos(storyFromDB);
        // then
        assertThat(childIssueDtos).hasSize(2);
        assertThat(childIssueDtos.get(0).getTitle()).isEqualTo("task1");
        assertThat(childIssueDtos.get(1).getTitle()).isEqualTo("task2");
        assertThat(childIssueDtos.get(0).getType()).isEqualTo("TASK");
        assertThat(childIssueDtos.get(0).getAssignee()).isNotNull();
        assertThat(childIssueDtos.get(0).getAssignee().getName()).isEqualTo("");

    }


    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }
}