package dynamicquad.agilehub.issue.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.config.MySQLTestContainer;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.dto.backlog.EpicResponseDto;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.sprint.domain.Sprint;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@Sql(scripts = "/sql/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SpringBootTest
@ExtendWith(MySQLTestContainer.class) // MySQL 컨테이너 적용
@ActiveProfiles("test") // test 환경 적용
class IssueRepositoryTest {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void 이슈_타입_조회_테스트() {
        // when
        Optional<String> issueType = issueRepository.findIssueTypeById(1L);

        // then
        assertThat(issueType).isPresent();
        assertThat(issueType.get()).isEqualTo("EPIC");
    }

    @Test
    void 특정_프로젝트에_이슈가_존재하는지_테스트() {
        // when
        boolean exists = issueRepository.existsByProjectIdAndId(1L, 1L);

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void 특정_스프린트에_속한_이슈_조회_테스트() {
        // given
        Sprint sprint = entityManager.getReference(Sprint.class, 1L);

        // when
        List<Issue> issues = issueRepository.findBySprint(sprint);

        // then
        assertThat(issues).isNotEmpty();
    }

    @Test
    void 특정_에픽에_속한_스토리_조회_테스트() {
        // when
        List<Issue> stories = issueRepository.findStoriesByEpicId(1L);

        // then
        assertThat(stories).isNotEmpty();
        assertThat(stories.get(0).getIssueType()).isEqualTo(IssueType.STORY);
    }

    @Test
    void 특정_스토리에_속한_태스크_조회_테스트() {
        // when
        List<Issue> tasks = issueRepository.findTasksByStoryId(2L);

        // then
        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getIssueType()).isEqualTo(IssueType.TASK);
    }

    @Test
    void 특정_프로젝트의_에픽_조회_테스트() {
        // given
        Project project = entityManager.getReference(Project.class, 1L);

        // when
        List<Issue> epics = issueRepository.findEpicsByProject(project);

        // then
        assertThat(epics).isNotEmpty();
        assertThat(epics.get(0).getIssueType()).isEqualTo(IssueType.EPIC);
    }

    @Test
    void 특정_프로젝트의_스토리_조회_테스트() {
        // given
        Project project = entityManager.getReference(Project.class, 1L);

        // when
        List<Issue> stories = issueRepository.findStoriesByProject(project);

        // then
        assertThat(stories).isNotEmpty();
        assertThat(stories.get(0).getIssueType()).isEqualTo(IssueType.STORY);
    }

    @Test
    void 특정_프로젝트의_태스크_조회_테스트() {
        // given
        Project project = entityManager.getReference(Project.class, 1L);

        // when
        List<Issue> tasks = issueRepository.findTasksByProject(project);

        // then
        assertThat(tasks).isNotEmpty();
        assertThat(tasks.get(0).getIssueType()).isEqualTo(IssueType.TASK);
    }

    @Test
    void 특정_날짜_범위_내_에픽_내용_조회_테스트() {
        // given
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 15);

        // when
        List<String> contents = issueRepository.findEpicContentsByMonth(startDate, endDate, 1L);

        // then
        assertThat(contents).isNotEmpty();
    }

    @Test
    void 특정_날짜_범위_내_스토리_내용_조회_테스트() {
        // given
        LocalDate startDate = LocalDate.of(2024, 3, 1);
        LocalDate endDate = LocalDate.of(2024, 3, 10);

        // when
        List<String> contents = issueRepository.findStoryContentsByMonth(startDate, endDate, 1L);

        // then
        assertThat(contents).isNotEmpty();
    }

    @Test
    void 특정_날짜_범위_내_태스크_내용_조회_테스트() {
        // given
        LocalDate startDate = LocalDate.of(2024, 3, 3);
        LocalDate endDate = LocalDate.of(2024, 3, 7);

        // when
        List<String> contents = issueRepository.findTaskContentsByMonth(startDate, endDate, 1L);

        // then
        assertThat(contents).isNotEmpty();
    }

    @Test
    @Sql(scripts = "/sql/epic-statistics-test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void 에픽_통계_기본_테스트() {
        // given
        Long projectId = 1L;

        // when
        List<EpicResponseDto.EpicStatistic> statistics = issueRepository.getEpicStatics(projectId);

        // then
        assertThat(statistics).isNotEmpty();

        // 각 에픽 통계 검증
        for (EpicResponseDto.EpicStatistic statistic : statistics) {
            assertThat(statistic.getEpicId()).isNotNull();
            assertThat(statistic.getStoriesCount()).isNotNull();
            // 상태별 카운트의 합이 전체 스토리 수와 일치해야 함
            assertThat(statistic.getStatusDo() + statistic.getStatusProgress() + statistic.getStatusDone())
                    .isEqualTo(statistic.getStoriesCount());
        }
    }

}