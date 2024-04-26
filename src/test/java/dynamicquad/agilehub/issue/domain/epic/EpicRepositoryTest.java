package dynamicquad.agilehub.issue.domain.epic;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.controller.response.EpicResponse.EpicStatisticDto;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.story.Story;
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
class EpicRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private EpicRepository epicRepository;

    @Test
    @Transactional
    void 프로젝트에_소속된_에픽이슈들을_조회한다() {
        // Given
        Project project1 = createProject("프로젝트1", "projec123t1");
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

    @Test
    @Transactional
    void 에픽에_있는_스토리_통계_정상적으로_가져오는지_확인() {
        // Given
        Project project1 = createProject("프로젝트1", "projec123t1");
        em.persist(project1);

        Epic epic1P1 = createEpic("에픽1", "에픽1 내용", project1);
        em.persist(epic1P1);
        Epic epic2P1 = createEpic("에픽2", "에픽2 내용", project1);
        em.persist(epic2P1);

        Story story1 = getStory(epic1P1, IssueStatus.DO, "스토리1", "스토리1 내용", project1);
        em.persist(story1);
        Story story2 = getStory(epic1P1, IssueStatus.PROGRESS, "스토리2", "스토리2 내용", project1);
        em.persist(story2);
        Story story3 = getStory(epic2P1, IssueStatus.DONE, "스토리3", "스토리3 내용", project1);
        em.persist(story3);

        em.flush();
        em.clear();
        // When
        List<EpicStatisticDto> epicStatics = epicRepository.getEpicStatics(project1.getId());
        // Then
        assertThat(epicStatics).hasSize(2);
        assertThat(epicStatics.get(0).getStoriesCount()).isEqualTo(2);
        assertThat(epicStatics.get(0).getStatusDo()).isEqualTo(1);
        assertThat(epicStatics.get(0).getStatusProgress()).isEqualTo(1);
        assertThat(epicStatics.get(0).getStatusDone()).isEqualTo(0);

    }

    private Story getStory(Epic epic1P1, IssueStatus status, String title, String content, Project project) {
        return Story.builder()
            .title(title)
            .content(content)
            .status(status)
            .project(project)
            .epic(epic1P1)
            .build();
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