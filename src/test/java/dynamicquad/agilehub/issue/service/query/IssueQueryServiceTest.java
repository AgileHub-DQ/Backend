package dynamicquad.agilehub.issue.service.query;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class IssueQueryServiceTest {

    @Autowired
    private IssueQueryService issueQueryService;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Transactional
    void 특정달의_이슈들을_가져오는지_테스트() {
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

        MonthlyReportDto issuesForMonth = issueQueryService.getIssuesForMonth("2024-01", project1.getId());
        // when

        // then
        assertThat(issuesForMonth.getContentsByEpic()).hasSize(2);
        assertThat(issuesForMonth.getContentsByStory()).hasSize(0);
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
            .startDate(LocalDate.of(2024, 1, 5))
            .endDate(LocalDate.of(2024, 3, 2))
            .build();
    }

    private Story createStory(String title, String content, Project project, Epic epic) {
        return Story.builder()
            .title(title)
            .content(content)
            .epic(epic)
            .status(IssueStatus.DO)
            .startDate(LocalDate.of(2024, 4, 1))
            .endDate(LocalDate.of(2024, 5, 2))
            .project(project)
            .build();
    }
}