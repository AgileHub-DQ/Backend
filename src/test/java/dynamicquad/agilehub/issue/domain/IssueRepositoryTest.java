package dynamicquad.agilehub.issue.domain;

import static org.assertj.core.api.Assertions.assertThat;

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
class IssueRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private IssueRepository issueRepository;

    @Test
    void 특정_프로젝트키를_가진_이슈들의_총_개수를_구한다() {
        // given
        Project project1 = createProject("프로젝트1", "project1");
        em.persist(project1);

        Project project2 = createProject("프로젝트2", "project2");
        em.persist(project2);

        Epic epic1P1 = createEpic("에픽1", "에픽1 내용", project1);
        em.persist(epic1P1);

        Epic epic2P1 = createEpic("에픽2", "에픽2 내용", project1);
        em.persist(epic2P1);

        Story story1P1 = createStory("스토리1", "스토리1 내용", project1);
        em.persist(story1P1);

        Story storyP2 = createStory("스토리2", "스토리2 내용", project2);
        em.persist(storyP2);

        Task taskP2 = createTask("태스크1", "태스크1 내용", project2);
        em.persist(taskP2);

        // when
        // then
        assertThat(issueRepository.countByProjectKey("project1")).isEqualTo(3);
        assertThat(issueRepository.countByProjectKey("project2")).isEqualTo(2);

    }


    @Test
    void 특정_프로젝트키를_가진_이슈가_없을때_0을_반환한다() {
        // given
        Project project1 = createProject("프로젝트1", "project1");
        em.persist(project1);
        // when
        // then
        assertThat(issueRepository.countByProjectKey("project1")).isEqualTo(0);
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
            .project(project)
            .build();
    }

    private Story createStory(String title, String content, Project project) {
        return Story.builder()
            .title(title)
            .content(content)
            .project(project)
            .build();
    }

    private Task createTask(String title, String content, Project project) {
        return Task.builder()
            .title(title)
            .content(content)
            .project(project)
            .build();
    }

}