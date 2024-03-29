package dynamicquad.agilehub.issue.domain.epic;

import static org.assertj.core.api.Assertions.assertThat;

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
@Transactional
class EpicRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private EpicRepository epicRepository;

    @Test
    void 프로젝트에_소속된_에픽이슈들을_조회한다() {
        // Given
        Project project1 = createProject("프로젝트1", "project1");
        em.persist(project1);

        Epic epic1P1 = createEpic("에픽1", "에픽1 내용", project1);
        em.persist(epic1P1);

        Epic epic2P1 = createEpic("에픽2", "에픽2 내용", project1);
        em.persist(epic2P1);
        // When
        List<Epic> byProject = epicRepository.findByProject(project1);
        // Then
        assertThat(byProject).hasSize(2);
        assertThat(byProject).containsExactlyInAnyOrder(epic1P1, epic2P1);
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

}