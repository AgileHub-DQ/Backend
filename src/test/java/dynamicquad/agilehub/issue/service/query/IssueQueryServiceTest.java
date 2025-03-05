package dynamicquad.agilehub.issue.service.query;

import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Java6Assertions.assertThat;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.dto.IssueResponseDto;
import dynamicquad.agilehub.issue.dto.backlog.EpicResponseDto;
import dynamicquad.agilehub.issue.dto.backlog.StoryResponseDto;
import dynamicquad.agilehub.issue.dto.backlog.TaskResponseDto;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.testSetUp.TestDataSetup;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class IssueQueryServiceTest {

    @Autowired
    private IssueQueryService issueQueryService;

    @Autowired
    private TestDataSetup testDataSetup;

    @Autowired
    private EntityManager em;

    private Project testProject;
    private Issue testEpic;
    private List<Issue> testStoriesInTestEpic = new ArrayList<>();
    private Issue testStory;
    private Member member;

    private static final String PROJECT_KEY = "TP";

    @BeforeEach
    void setUp() {
        testProject = testDataSetup.createAndSaveProject("testProject", "TP");
        member = testDataSetup.createAndSaveMember("testMember", MemberStatus.ACTIVE);
        MemberProject memberProject = testDataSetup.createAndSaveMemberProject(testProject, member,
            MemberProjectRole.ADMIN);

        testEpic = testDataSetup.createIssue(testProject, "테스트 에픽", IssueType.EPIC, member);
        em.persist(testEpic);

        testStory = testDataSetup.createIssue(testProject, "테스트 스토리", IssueType.STORY, member);
        testStoriesInTestEpic.add(testStory);
        Issue testStory2 = testDataSetup.createIssue(testProject, "테스트 스토리", IssueType.STORY, member);
        testStoriesInTestEpic.add(testStory2);

        testStory.setParentIssue(testEpic);
        testStory2.setParentIssue(testEpic);

        em.persist(testStory);
        em.persist(testStory2);
        em.flush();
    }

    @Test
    @DisplayName("이슈 상세 조회 테스트 (에픽일때)")
    void getIssueTest() {
        // when
        IssueResponseDto.IssueAndSubIssueDetail result = issueQueryService.getIssue(PROJECT_KEY, testEpic.getId(),
            AuthMember.builder().id(member.getId()).build());

        //result = IssueResponseDto.IssueAndSubIssueDetail(issue=IssueResponseDto.IssueDetail(issueId=1, key=TEST-X, title=테스트 에픽, type=EPIC, status=DO, label=TEST, startDate=2021-01-01, endDate=2021-01-02, content=IssueResponseDto.ContentDto(text=content, imagesURLs=[]), assignee=AssigneeDto(id=1, name=testMember, profileImageURL=www.naver.com)), parentIssue=IssueResponseDto.SubIssueDetail(issueId=null, key=, status=, label=null, type=, title=, assignee=AssigneeDto(id=null, name=, profileImageURL=)), childIssues=[IssueResponseDto.SubIssueDetail(issueId=2, key=TEST-X, status=DO, label=TEST, type=STORY, title=테스트 스토리, assignee=AssigneeDto(id=1, name=testMember, profileImageURL=www.naver.com))])
        // then
        assertThat(result.getIssue())
            .extracting("issueId", "key", "title", "type", "status", "label", "startDate", "endDate", "content.text",
                "assignee.id", "assignee.name")
            .containsExactly(
                testEpic.getId(),
                testEpic.getNumber(),
                testEpic.getTitle(),
                IssueType.EPIC.toString(),
                testEpic.getStatus().toString(),
                String.valueOf(testEpic.getLabel()),
                String.valueOf(testEpic.getStartDate()),
                String.valueOf(testEpic.getEndDate()),
                testEpic.getContent(),
                member.getId(),
                member.getName()
            );

        assertThat(result.getParentIssue().getIssueId()).isNull();
        assertThat(result.getChildIssues())
            .hasSize(testStoriesInTestEpic.size())
            .extracting("issueId", "key", "title", "type", "status", "label", "assignee.id", "assignee.name")
            .contains(
                tuple(testStory.getId(), testStory.getNumber(), testStory.getTitle(), IssueType.STORY.toString(),
                    testStory.getStatus().toString(), String.valueOf(testStory.getLabel()), member.getId(),
                    member.getName())
            );


    }

    // TODO: 이 메서드에서 EpicStatistic 인터페이스를 사용하고 있어, 테스트 복잡성 때문에 통합테스트로 변경함
    @Test
    @DisplayName("에픽 통계 조회 테스트")
    void getEpicsWithStatsTest() {
        // given
        Issue testEpic2 = testDataSetup.createIssue(testProject, "테스트 에픽2", IssueType.EPIC, member);
        em.persist(testEpic2);
        em.flush();

        // when
        List<EpicResponseDto.EpicDetailWithStatistic> result = issueQueryService.getEpicsWithStats(
            PROJECT_KEY, AuthMember.builder().id(member.getId()).build());

//        System.out.println(result);
//        EpicStatistic statistic = result.get(0).getStatistic();
//        System.out.println("epicId: " + statistic.getEpicId() +
//            ", storiesCount: " + statistic.getStoriesCount() +
//            ", statusDo: " + statistic.getStatusDo() +
//            ", statusProgress: " + statistic.getStatusProgress() +
//            ", statusDone: " + statistic.getStatusDone());

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIssue())
            .extracting("id", "title", "key", "status", "type", "label", "startDate", "endDate", "assignee.id",
                "assignee.name")
            .containsExactly(
                testEpic.getId(),
                testEpic.getTitle(),
                testEpic.getNumber(),
                testEpic.getStatus().toString(),
                IssueType.EPIC.toString(),
                String.valueOf(testEpic.getLabel()),
                String.valueOf(testEpic.getStartDate()),
                String.valueOf(testEpic.getEndDate()),
                member.getId(),
                member.getName()
            );

        assertThat(result.get(0).getStatistic())
            .extracting("epicId", "storiesCount", "statusDo", "statusProgress", "statusDone")
            .containsExactly(
                testEpic.getId(),
                2L,
                2L,
                0L,
                0L
            );
    }

    @Test
    @DisplayName("스토리 조회 테스트 (에픽별)")
    void getStoriesByEpicTest() {
        // when
        List<StoryResponseDto.StoryDetailForBacklog> result = issueQueryService.getStoriesByEpic(PROJECT_KEY,
            testEpic.getId(),
            AuthMember.builder().id(member.getId()).build());

        // then
        assertThat(result)
            .hasSize(testStoriesInTestEpic.size())
            .extracting("id", "title", "key", "status", "label", "type", "startDate", "endDate", "parentId",
                "assignee.id", "assignee.name")
            .contains(
                tuple(
                    testStory.getId(),
                    testStory.getTitle(),
                    testStory.getNumber(),
                    testStory.getStatus().toString(),
                    String.valueOf(testStory.getLabel()),
                    IssueType.STORY.toString(),
                    String.valueOf(testStory.getStartDate()),
                    String.valueOf(testStory.getEndDate()),
                    testEpic.getId(),
                    member.getId(),
                    member.getName()
                )
            );
    }

    @Test
    @DisplayName("테스트 조회 테스트 (스토리별)")
    void getTasksByStoryTest() {
        // given
        Issue testTask = testDataSetup.createIssue(testProject, "테스트 태스크", IssueType.TASK, member);
        testTask.setParentIssue(testStory);
        em.persist(testTask);
        em.flush();

        // when
        List<TaskResponseDto.TaskDetailForBacklog> result = issueQueryService.getTasksByStory(PROJECT_KEY,
            testStory.getId(),
            AuthMember.builder().id(member.getId()).build());

        // then
        assertThat(result)
            .hasSize(1)
            .extracting("id", "title", "key", "status", "label", "type", "startDate", "endDate", "parentId",
                "assignee.id", "assignee.name")
            .contains(
                tuple(
                    testTask.getId(),
                    testTask.getTitle(),
                    testTask.getNumber(),
                    testTask.getStatus().toString(),
                    String.valueOf(testTask.getLabel()),
                    IssueType.TASK.toString(),
                    String.valueOf(testTask.getStartDate()),
                    String.valueOf(testTask.getEndDate()),
                    testStory.getId(),
                    member.getId(),
                    member.getName()
                )
            );
    }

    @Test
    @DisplayName("에픽 조회 테스트")
    void getEpicsTest() {
        // given
        Issue testEpic2 = testDataSetup.createIssue(testProject, "테스트 에픽2", IssueType.EPIC, member);
        em.persist(testEpic2);
        em.flush();

        // when
        List<IssueResponseDto.ReadSimpleIssue> result = issueQueryService.getEpics(PROJECT_KEY,
            AuthMember.builder().id(member.getId()).build());

        // then
        assertThat(result)
            .hasSize(2)
            .extracting("id", "title", "key", "status", "type", "label", "assignee.id", "assignee.name")
            .contains(
                tuple(
                    testEpic.getId(),
                    testEpic.getTitle(),
                    testEpic.getNumber(),
                    testEpic.getStatus().toString(),
                    IssueType.EPIC.toString(),
                    String.valueOf(testEpic.getLabel()),
                    member.getId(),
                    member.getName()
                )
            );
    }

    @Test
    @DisplayName("스토리 조회 테스트")
    void getStoriesTest() {
        // when
        List<IssueResponseDto.ReadSimpleIssue> result = issueQueryService.getStories(PROJECT_KEY,
            AuthMember.builder().id(member.getId()).build());

        // then
        assertThat(result)
            .hasSize(2)
            .extracting("id", "title", "key", "status", "type", "label", "assignee.id", "assignee.name")
            .contains(
                tuple(
                    testStory.getId(),
                    testStory.getTitle(),
                    testStory.getNumber(),
                    testStory.getStatus().toString(),
                    IssueType.STORY.toString(),
                    String.valueOf(testStory.getLabel()),
                    member.getId(),
                    member.getName()
                )
            );
    }

    @Test
    @DisplayName("태스크 조회 테스트")
    void getTasksTest() {
        // given
        Issue testTask = testDataSetup.createIssue(testProject, "테스트 태스크", IssueType.TASK, member);
        em.persist(testTask);
        em.flush();

        // when
        List<IssueResponseDto.ReadSimpleIssue> result = issueQueryService.getTasks(PROJECT_KEY,
            AuthMember.builder().id(member.getId()).build());

        // then
        assertThat(result)
            .hasSize(1)
            .extracting("id", "title", "key", "status", "type", "label", "assignee.id", "assignee.name")
            .contains(
                tuple(
                    testTask.getId(),
                    testTask.getTitle(),
                    testTask.getNumber(),
                    testTask.getStatus().toString(),
                    IssueType.TASK.toString(),
                    String.valueOf(testTask.getLabel()),
                    member.getId(),
                    member.getName()
                )
            );
    }


}