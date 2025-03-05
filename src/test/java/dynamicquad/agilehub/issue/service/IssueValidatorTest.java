package dynamicquad.agilehub.issue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.testSetUp.TestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class IssueValidatorTest {

    @Autowired
    private IssueValidator issueValidator;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private TestDataSetup testDataSetup;

    private Project project;
    private Member member;
    private Issue epicIssue;
    private Issue storyIssue;
    private Issue taskIssue;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        project = testDataSetup.createAndSaveProject("테스트 프로젝트", "TEST");
        member = testDataSetup.createAndSaveMember("테스트 사용자", MemberStatus.ACTIVE);
        testDataSetup.createAndSaveMemberProject(project, member, MemberProjectRole.ADMIN);

        // Epic 이슈 생성
        IssueRequestDto.CreateIssue epicRequest = IssueRequestDto.CreateIssue.builder()
                .title("에픽 이슈")
                .content("에픽 내용")
                .status(IssueStatus.DO)
                .type(IssueType.EPIC)
                .build();
        epicIssue = Issue.createEpic(epicRequest, member, "TEST-1", project);
        issueRepository.save(epicIssue);

        // Story 이슈 생성
        IssueRequestDto.CreateIssue storyRequest = IssueRequestDto.CreateIssue.builder()
                .title("스토리 이슈")
                .content("스토리 내용")
                .status(IssueStatus.DO)
                .type(IssueType.STORY)
                .parentId(epicIssue.getId())
                .build();
        storyIssue = Issue.createStory(storyRequest, member, "TEST-2", project);
        issueRepository.save(storyIssue);

        // Task 이슈 생성
        IssueRequestDto.CreateIssue taskRequest = IssueRequestDto.CreateIssue.builder()
                .title("태스크 이슈")
                .content("태스크 내용")
                .status(IssueStatus.DO)
                .type(IssueType.TASK)
                .parentId(storyIssue.getId())
                .build();
        taskIssue = Issue.createTask(taskRequest, member, "TEST-3", project);
        issueRepository.save(taskIssue);
    }

    @Test
    @DisplayName("validateIssueInProject: 프로젝트에 이슈가 존재하는 경우 예외가 발생하지 않는다")
    void validateIssueInProject_Success() {
        // when & then
        assertDoesNotThrow(() -> issueValidator.validateIssueInProject(project.getId(), epicIssue.getId()));
    }

    @Test
    @DisplayName("validateIssueInProject: 프로젝트에 이슈가 존재하지 않는 경우 예외가 발생한다")
    void validateIssueInProject_Fail() {
        // given
        Long nonExistingProjectId = 9999L;

        // when & then
        assertThatThrownBy(() -> issueValidator.validateIssueInProject(nonExistingProjectId, epicIssue.getId()))
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("status", ErrorStatus.ISSUE_NOT_IN_PROJECT);
    }

    @Test
    @DisplayName("findIssue: 존재하는 이슈를 찾을 수 있다")
    void findIssue_Success() {
        // when
        Issue foundIssue = issueValidator.findIssue(epicIssue.getId());

        // then
        assertThat(foundIssue).isNotNull();
        assertThat(foundIssue.getId()).isEqualTo(epicIssue.getId());
    }

    @Test
    @DisplayName("findIssue: 존재하지 않는 이슈를 찾으려고 하면 예외가 발생한다")
    void findIssue_Fail() {
        // given
        Long nonExistingIssueId = 9999L;

        // when & then
        assertThatThrownBy(() -> issueValidator.findIssue(nonExistingIssueId))
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("status", ErrorStatus.ISSUE_NOT_FOUND);
    }

    @Test
    @DisplayName("validateEqualsIssueType: 이슈 타입이 일치하는 경우 예외가 발생하지 않는다")
    void validateEqualsIssueType_Success() {
        // when & then
        assertDoesNotThrow(() -> issueValidator.validateEqualsIssueType(epicIssue, IssueType.EPIC));
    }

    @Test
    @DisplayName("validateEqualsIssueType: 이슈 타입이 일치하지 않는 경우 예외가 발생한다")
    void validateEqualsIssueType_Fail() {
        // when & then
        assertThatThrownBy(() -> issueValidator.validateEqualsIssueType(epicIssue, IssueType.STORY))
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("status", ErrorStatus.ISSUE_TYPE_MISMATCH);
    }

    @Test
    @DisplayName("getIssueType: 이슈 타입을 정확히 가져올 수 있다")
    void getIssueType_Success() {
        // when
        IssueType epicType = issueValidator.getIssueType(epicIssue.getId());
        IssueType storyType = issueValidator.getIssueType(storyIssue.getId());
        IssueType taskType = issueValidator.getIssueType(taskIssue.getId());

        // then
        assertThat(epicType).isEqualTo(IssueType.EPIC);
        assertThat(storyType).isEqualTo(IssueType.STORY);
        assertThat(taskType).isEqualTo(IssueType.TASK);
    }

    @Test
    @DisplayName("getIssueType: 존재하지 않는 이슈의 타입을 가져오려고 하면 예외가 발생한다")
    void getIssueType_Fail() {
        // given
        Long nonExistingIssueId = 9999L;

        // when & then
        assertThatThrownBy(() -> issueValidator.getIssueType(nonExistingIssueId))
                .isInstanceOf(GeneralException.class)
                .hasFieldOrPropertyWithValue("status", ErrorStatus.ISSUE_TYPE_NOT_FOUND);
    }
}