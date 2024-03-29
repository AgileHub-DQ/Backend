package dynamicquad.agilehub.issue.service;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.controller.response.IssueHierarchyResponse;
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
class IssueQueryServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private IssueQueryService issueQueryService;


    @Test
    @Transactional
    void 프로젝트에_속한_이슈들을_모두_조회() {
        // given
        Project project1 = createProject("프로젝트1", "project12311");
        em.persist(project1);
        Epic epic1P1 = createEpic("에픽1", "에픽1 내용", project1);
        em.persist(epic1P1);
        Epic epic2P1 = createEpic("에픽2", "에픽2 내용", project1);
        em.persist(epic2P1);
        Story story1P1 = createStory("스토리1", "스토리1 내용", project1, epic2P1);
        em.persist(story1P1);
        Story story2P1 = createStory("스토리2", "스토리2 내용", project1, epic2P1);
        em.persist(story2P1);
        // when
        List<IssueHierarchyResponse> IssueHierarchyResponses = issueQueryService.getIssues("project12311");
        // then
        assertThat(IssueHierarchyResponses).hasSize(2);
    }

    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }

    private Epic createEpic(String title, String content, Project project) {
        return Epic.builder()
            .title(title)
            .content(content)
            .status(IssueStatus.DO)
            .project(project)
            .build();
    }

    private Story createStory(String title, String content, Project project, Epic epic) {
        return Story.builder()
            .title(title)
            .content(content)
            .epic(epic)
            .status(IssueStatus.DO)
            .project(project)
            .build();
    }

    private Task createTask(String title, String content, Project project) {
        return Task.builder()
            .title(title)
            .content(content)
            .status(IssueStatus.DO)
            .project(project)
            .build();
    }

}