package dynamicquad.agilehub.issue.service.command;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.config.RedisTestContainer;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueLabel;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.ProjectIssueSequence;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.dto.IssueRequestDto.CreateIssue;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.testSetUp.TestDataSetup;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@ExtendWith(RedisTestContainer.class)
class IssueServiceTest {

    @Autowired
    private IssueService issueService;

    @Autowired
    private TestDataSetup testDataSetup;

    @Autowired
    private EntityManager em;

    private Project testProject;
    private Issue testEpic;
    private List<Issue> testStoriesInTestEpic = new ArrayList<>();
    private Issue testStory;
    private Member member;

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
    @DisplayName("이슈를 정상적으로 생성해야 한다")
    void createIssueTest() {

        // given

        CreateIssue createRequest = createIssue("테스트 이슈", "설명", IssueStatus.DO, IssueLabel.TEST);

        // when
        Long issueId = issueService.createIssue(testProject.getKey(), createRequest,
            AuthMember.builder().id(member.getId()).build());

        // then
        Issue createdIssue = em.find(Issue.class, issueId);

        assertThat(createdIssue.getTitle()).isEqualTo("테스트 이슈");
        assertThat(createdIssue.getIssueType()).isEqualTo(IssueType.EPIC);
        assertThat(createdIssue.getStatus()).isEqualTo(IssueStatus.DO);
        assertThat(createdIssue.getLabel()).isEqualTo(IssueLabel.TEST);
        assertThat(createdIssue.getContent()).isEqualTo("설명");
        assertThat(createdIssue.getAssignee().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("이슈 상태를 변경할 수 있어야 한다")
    void updateIssueStatusTest() {
        // given
        IssueStatus newStatus = IssueStatus.PROGRESS;

        // when
        issueService.updateIssueStatus(testProject.getKey(), testStory.getId(),
            AuthMember.builder().id(member.getId()).build(), newStatus);

        // then
        Issue updatedIssue = em.find(Issue.class, testStory.getId());
        assertThat(updatedIssue.getStatus()).isEqualTo(newStatus);
    }

    @Test
    @DisplayName("이슈를 정상적으로 삭제해야 한다")
    void deleteIssueTest() {
        // given
        // 프로젝트 생성을 ProjectService에서 처리해야 이슈시퀀스테이블이 업데이트됨 따라서 강제 업데이트하자
        em.persist(new ProjectIssueSequence(testProject.getKey()));
        em.flush();

        CreateIssue createRequest = createIssue("삭제 이슈", "설명", IssueStatus.DO, IssueLabel.TEST);
        Long issueId = issueService.createIssue(testProject.getKey(), createRequest,
            AuthMember.builder().id(member.getId()).build());

        // when
        issueService.deleteIssue(testProject.getKey(), issueId, AuthMember.builder().id(member.getId()).build());

        // then
        Issue deletedIssue = em.find(Issue.class, issueId);
        assertThat(deletedIssue).isNull();
    }

    @Test
    @DisplayName("이슈를 정상적으로 수정할 수 있어야 한다")
    void updateIssueTest() {
        // given
        Issue beforeIssue = testDataSetup.createIssue(testProject, "수정 전 이슈", IssueType.EPIC, member);
        em.persist(beforeIssue);

        IssueRequestDto.EditIssue updateRequest = IssueRequestDto.EditIssue.builder()
            .title("수정된 이슈")
            .content("수정된 설명")
            .status(IssueStatus.PROGRESS)
            .label(IssueLabel.DESIGN)
            .type(IssueType.EPIC)
            .build();

        // when
        issueService.updateIssue(testProject.getKey(), beforeIssue.getId(), updateRequest,
            AuthMember.builder().id(member.getId()).build());

        // then
        Issue updatedIssue = em.find(Issue.class, beforeIssue.getId());
        assertThat(updatedIssue.getTitle()).isEqualTo("수정된 이슈");
        assertThat(updatedIssue.getContent()).isEqualTo("수정된 설명");
        assertThat(updatedIssue.getStatus()).isEqualTo(IssueStatus.PROGRESS);
        assertThat(updatedIssue.getLabel()).isEqualTo(IssueLabel.DESIGN);
    }


    @Test
    @DisplayName("이슈 기간을 정상적으로 수정할 수 있어야 한다")
    void updateIssuePeriodTest() {
        // given
        Issue beforeIssue = testDataSetup.createIssue(testProject, "기간 수정 전 이슈", IssueType.EPIC, member);
        beforeIssue.updatePeriod(LocalDate.of(2024, 3, 3), LocalDate.of(2024, 3, 5));
        em.persist(beforeIssue);

        IssueRequestDto.EditIssuePeriod editPeriodRequest = new IssueRequestDto.EditIssuePeriod(
            LocalDate.of(2024, 3, 1),
            LocalDate.of(2024, 3, 10));

        // when
        issueService.updateIssuePeriod(testProject.getKey(), beforeIssue.getId(),
            AuthMember.builder().id(member.getId()).build(),
            editPeriodRequest);
        Issue updatedIssue = em.find(Issue.class, beforeIssue.getId());

        // then
        assertThat(updatedIssue.getStartDate()).isEqualTo("2024-03-01");
        assertThat(updatedIssue.getEndDate()).isEqualTo("2024-03-10");
    }

    private CreateIssue createIssue(String title, String content, IssueStatus status, IssueLabel label) {
        return CreateIssue.builder()
            .title(title)
            .content(content)
            .status(status)
            .label(label)
            .files(new ArrayList<>())
            .startDate(null)
            .endDate(null)
            .assigneeId(member.getId())
            .type(IssueType.EPIC)
            .parentId(null)
            .build();
    }
}