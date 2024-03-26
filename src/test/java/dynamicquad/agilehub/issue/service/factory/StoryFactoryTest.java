package dynamicquad.agilehub.issue.service.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.issue.domain.Task;
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
@Transactional
class StoryFactoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private StoryFactory storyFactory;


    @Test
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
    void 부모에픽을_정상적으로_찾는다() {
        // given
        Project project = createProject("프로젝트1", "project1");
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
    void 부모이슈가_정상적으로_등록() {
        // given
        Project project = createProject("프로젝트1", "project1");
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
        Story story = em.createQuery("select s from Story s where s.id = :issueId", Story.class)
            .setParameter("issueId", issueId)
            .getSingleResult();

        assertThat(story.getEpic().getTitle()).isEqualTo("에픽 제목");


    }

    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }
}