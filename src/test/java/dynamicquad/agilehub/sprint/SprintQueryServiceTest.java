package dynamicquad.agilehub.sprint;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.sprint.controller.response.SprintResponse.SprintReadResponse;
import dynamicquad.agilehub.sprint.domain.Sprint;
import dynamicquad.agilehub.sprint.service.SprintQueryService;
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
class SprintQueryServiceTest {

    @Autowired
    private SprintQueryService sprintQueryService;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Transactional
    void 스프린트내에_있는_이슈들_모두_조회() {
        // given
        prepareData();
        // when
        List<SprintReadResponse> results = sprintQueryService.getSprints("P1");
        // then
        assertThat(results).hasSize(1);
        assertThat(results).extracting("title")
            .containsExactly("sprint1");
        assertThat(results).extracting("issueCount")
            .containsExactly(2L);


    }

    private void prepareData() {
        Project project = createProject("project", "P1");
        em.persist(project);
        Epic epic = createEpic("epic1", "content1", project);
        Story story = createStory("story1", "content1", project);
        Task task = createTask("task1", "content1", project);
        em.persist(epic);
        em.persist(story);
        em.persist(task);
        Sprint sprint = Sprint.builder()
            .title("sprint1")
            .build();
        sprint.setProject(project);
        em.persist(sprint);
        story.setSprint(sprint);
        task.setSprint(sprint);

        Task taskNotExist = createTask("task2", "content2", project);
        em.persist(taskNotExist);
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
            .number(1)
            .project(project)
            .build();
    }

    private Story createStory(String title, String content, Project project) {
        return Story.builder()
            .title(title)
            .content(content)
            .project(project)
            .number(2)
            .build();
    }

    private Task createTask(String title, String content, Project project) {
        return Task.builder()
            .title(title)
            .content(content)
            .project(project)
            .number(3)
            .build();
    }

}