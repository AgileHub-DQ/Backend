package dynamicquad.agilehub.issue.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class IssueRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private IssueRepository issueRepository;


    @Test
    @Transactional
    void 스토리이슈의_타입을_조회하면_story_string을_반환한다() {
        // given
        Project project1 = createProject("프로젝트1", "project11214");
        em.persist(project1);

        Story story1P1 = createStory("스토리1", "스토리1 내용", project1);
        em.persist(story1P1);

        // when
        String issueType = issueRepository.findIssueTypeById(story1P1.getId())
            .orElseThrow(() -> new IllegalArgumentException("이슈가 없습니다."));
        // then
        assertThat(issueType).isEqualTo("STORY");

    }

    @Test
    @Transactional
    void 없는_이슈를_타입조회하면_빈_Optional을_반환한다() {
        // given
        // when
        Optional<String> issueType = issueRepository.findIssueTypeById(1L);
        // then
        assertThat(issueType).isEmpty();
    }

    @Test
    @Transactional
    void 프로젝트에_소속된_이슈들을_조회한다() {
        // given
        Project project1 = createProject("프로젝트1", "project1231");
        em.persist(project1);

        Epic epic1P1 = createEpic("에픽1", "에픽1 내용", project1);
        em.persist(epic1P1);

        Epic epic2P1 = createEpic("에픽2", "에픽2 내용", project1);
        em.persist(epic2P1);

        Story story1P1 = createStory("스토리1", "스토리1 내용", project1);
        em.persist(story1P1);

        // when
        List<Issue> byProject = issueRepository.findByProject(project1);

        // then
        assertThat(byProject).hasSize(3);
        assertThat(byProject.get(0).getTitle()).isEqualTo("에픽1");
        assertThat(byProject.get(1).getTitle()).isEqualTo("에픽2");
        assertThat(byProject.get(2).getTitle()).isEqualTo("스토리1");
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