package dynamicquad.agilehub.issue.service.command;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.dto.IssueRequestDto.CreateIssue;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class IssueServiceTest {
    @Autowired
    private IssueService issueService;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    private Project project;
    private AuthMember authMember;

    @BeforeEach
    void setUp() {
        // 프로젝트 생성
        project = Project.builder()
            .name("Test Project")
            .key("TEST-123")
            .build();
        projectRepository.save(project);

        // 테스트 유저 생성
        authMember = AuthMember.builder()
            .id(1L)
            .build();
    }

    @Test
    @DisplayName("이슈를 정상적으로 생성해야 한다")
    void createIssueTest() {
        // given
        CreateIssue createRequest = CreateIssue.builder()
            .title("이슈 제목")
            .type(IssueType.EPIC)
            .status(IssueStatus.DO)
            .build();

        // when
        Long issueId = issueService.createIssue(project.getKey(), createRequest, authMember);
        Issue issue = issueRepository.findById(issueId).orElseThrow();

        // then
        assertThat(issue.getTitle()).isEqualTo("이슈 제목");
        assertThat(issue.getStatus()).isEqualTo(IssueStatus.DO);
    }

//    @Test
//    @DisplayName("이슈 상태를 변경할 수 있어야 한다")
//    void updateIssueStatusTest() {
//        // given
//        CreateIssue createRequest = new CreateIssue("테스트 이슈", "설명", IssueStatus.TODO, null, null);
//        Long issueId = issueService.createIssue(project.getKey(), createRequest, authMember);
//
//        // when
//        issueService.updateIssueStatus(project.getKey(), issueId, authMember, IssueStatus.IN_PROGRESS);
//        Issue updatedIssue = issueRepository.findById(issueId).orElseThrow();
//
//        // then
//        assertThat(updatedIssue.getStatus()).isEqualTo(IssueStatus.IN_PROGRESS);
//    }
//
//    @Test
//    @DisplayName("이슈를 정상적으로 삭제해야 한다")
//    void deleteIssueTest() {
//        // given
//        CreateIssue createRequest = new CreateIssue("삭제할 이슈", "설명", IssueStatus.TODO, null, null);
//        Long issueId = issueService.createIssue(project.getKey(), createRequest, authMember);
//
//        // when
//        issueService.deleteIssue(project.getKey(), issueId, authMember);
//
//        // then
//        assertThat(issueRepository.findById(issueId)).isEmpty();
//    }
//
//    @Test
//    @DisplayName("이슈 기간을 정상적으로 수정할 수 있어야 한다")
//    void updateIssuePeriodTest() {
//        // given
//        CreateIssue createRequest = new CreateIssue("기간 수정 이슈", "설명", IssueStatus.TODO, null, null);
//        Long issueId = issueService.createIssue(project.getKey(), createRequest, authMember);
//
//        IssueRequestDto.EditIssuePeriod editPeriodRequest = new IssueRequestDto.EditIssuePeriod("2024-03-01",
//            "2024-03-10");
//
//        // when
//        issueService.updateIssuePeriod(project.getKey(), issueId, authMember, editPeriodRequest);
//        Issue updatedIssue = issueRepository.findById(issueId).orElseThrow();
//
//        // then
//        assertThat(updatedIssue.getStartDate()).isEqualTo("2024-03-01");
//        assertThat(updatedIssue.getEndDate()).isEqualTo("2024-03-10");
//    }
}