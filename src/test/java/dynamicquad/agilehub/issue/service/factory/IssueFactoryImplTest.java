package dynamicquad.agilehub.issue.service.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueLabel;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.dto.IssueRequestDto.CreateIssue;
import dynamicquad.agilehub.issue.dto.IssueRequestDto.EditIssue;
import dynamicquad.agilehub.issue.dto.IssueResponseDto.ContentDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto.IssueDetail;
import dynamicquad.agilehub.issue.dto.IssueResponseDto.SubIssueDetail;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.issue.service.command.ImageService;
import dynamicquad.agilehub.issue.service.command.IssueNumberGenerator;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.member.service.MemberService;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.testSetUp.TestDataSetup;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class IssueFactoryImplTest {
    @Autowired
    private IssueFactory issueFactory;

    @Autowired
    private IssueRepository issueRepository;

    @MockBean
    private ImageService imageService;

    @MockBean
    private IssueNumberGenerator issueNumberGenerator;

    @MockBean
    private MemberService memberService;

    @Autowired
    private TestDataSetup testDataSetup;

    private Project testProject;
    private Member testMember;


    @BeforeEach
    void setUp() {
        // 각 테스트 실행 전에 기본 데이터 준비
        testProject = testDataSetup.createAndSaveProject("테스트 프로젝트", "TEST");
        testMember = testDataSetup.createAndSaveMember("테스트 멤버", MemberStatus.ACTIVE);
        testDataSetup.createAndSaveMemberProject(testProject, testMember, MemberProjectRole.ADMIN);
    }

    @Test
    @DisplayName("이슈 생성 - EPIC 타입")
    void createEpicIssue() {
        // given
        CreateIssue request = createIssueRequest(IssueType.EPIC);

        when(issueNumberGenerator.generate(anyString())).thenReturn("TEST-1");
        when(memberService.findMember(anyLong(), anyLong())).thenReturn(testMember);

        // when
        Long issueId = issueFactory.createIssue(request, testProject);

        // then
        Issue savedIssue = issueRepository.findById(issueId).orElseThrow();
        assertThat(savedIssue).isNotNull();
        assertThat(savedIssue.getIssueType()).isEqualTo(IssueType.EPIC);
        assertThat(savedIssue.getNumber()).isEqualTo("TEST-1");
        assertThat(savedIssue.getContent()).isEqualTo("테스트 내용");
        assertThat(savedIssue.getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("이슈 생성 - STORY 타입")
    void createStoryIssue() {
        // given
        CreateIssue request = createIssueRequest(IssueType.STORY);

        when(issueNumberGenerator.generate(anyString())).thenReturn("TEST-2");
        when(memberService.findMember(anyLong(), anyLong())).thenReturn(testMember);

        // when
        Long issueId = issueFactory.createIssue(request, testProject);

        // then
        Issue savedIssue = issueRepository.findById(issueId).orElseThrow();
        assertThat(savedIssue).isNotNull();
        assertThat(savedIssue.getIssueType()).isEqualTo(IssueType.STORY);
        assertThat(savedIssue.getNumber()).isEqualTo("TEST-2");
    }

    @Test
    @DisplayName("이슈 업데이트")
    void updateIssue() {
        // given
        CreateIssue createRequest = createIssueRequest(IssueType.TASK);

        when(issueNumberGenerator.generate(anyString())).thenReturn("TEST-3");
        when(memberService.findMember(anyLong(), anyLong())).thenReturn(testMember);

        Long issueId = issueFactory.createIssue(createRequest, testProject);
        Issue issue = issueRepository.findById(issueId).orElseThrow();

        EditIssue editRequest = EditIssue.builder()
            .title("수정된 제목")
            .content("수정된 내용")
            .assigneeId(2L)
            .type(IssueType.TASK)
            .status(IssueStatus.PROGRESS)
            .label(IssueLabel.DEVELOP)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(5))
            .imageUrls(new ArrayList<>())
            .build();

        Member newAssignee = Mockito.mock(Member.class);
        when(newAssignee.getId()).thenReturn(2L);
        when(newAssignee.getName()).thenReturn("새 담당자");

        when(memberService.findMember(anyLong(), anyLong())).thenReturn(newAssignee);

        // when
        Long updatedIssueId = issueFactory.updateIssue(issue, testProject, editRequest);

        // then
        Issue updatedIssue = issueRepository.findById(updatedIssueId).orElseThrow();
        assertThat(updatedIssue.getTitle()).isEqualTo("수정된 제목");
        assertThat(updatedIssue.getContent()).isEqualTo("수정된 내용");
        assertThat(updatedIssue.getAssignee().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("ContentDto 생성")
    void createContentDto() {
        // given
        Issue issue = Issue.createTask(
            createIssueRequest(IssueType.TASK),
            testMember,
            "TEST-4",
            testProject);
        issueRepository.save(issue);

        // when
        ContentDto contentDto = issueFactory.createContentDto(issue);

        // then
        assertThat(contentDto).isNotNull();
        assertThat(contentDto.getText()).isEqualTo("테스트 내용");
    }

    @Test
    @DisplayName("IssueDetail 생성")
    void createIssueDetail() {
        // given
        Issue issue = Issue.createTask(
            createIssueRequest(IssueType.TASK),
            testMember,
            "TEST-5",
            testProject);
        issueRepository.save(issue);

        ContentDto contentDto = ContentDto.from(issue);
        AssigneeDto assigneeDto = AssigneeDto.from(issue);

        // when
        IssueDetail issueDetail = issueFactory.createIssueDetail(issue, contentDto, assigneeDto);

        // then
        assertThat(issueDetail).isNotNull();
        assertThat(issueDetail.getTitle()).isEqualTo("테스트 제목");
        assertThat(issueDetail.getKey()).isEqualTo("TEST-5");
        assertThat(issueDetail.getContent().getText()).isEqualTo("테스트 내용");
        assertThat(issueDetail.getAssignee().getName()).isEqualTo("테스트 멤버");
    }

    @Test
    @DisplayName("상위 이슈 생성 - 부모 이슈가 없는 경우(에픽)")
    void createParentIssueByEpic() {
        // given
        Issue parentIssue = Issue.createEpic(
            createIssueRequest(IssueType.EPIC),
            testMember,
            "TEST-6",
            testProject);
        issueRepository.save(parentIssue);

        // when
        SubIssueDetail parentIssueDto = issueFactory.createParentIssue(parentIssue);

        // then
        assertThat(parentIssueDto).isNotNull();
        assertThat(parentIssueDto.getKey()).isEqualTo("");
    }

    @Test
    @DisplayName("상위 이슈 생성 - 부모 이슈가 있는 경우(스토리)")
    void createParentIssueByStory() {
        // given
        Issue parentIssue = Issue.createEpic(
            createIssueRequest(IssueType.EPIC),
            testMember,
            "TEST-6",
            testProject);
        issueRepository.save(parentIssue);

        CreateIssue issueRequest = createIssueRequest(IssueType.STORY);
        issueRequest.setParentId(parentIssue.getId());
        Issue subIssue = Issue.createStory(
            issueRequest,
            testMember,
            "TEST-7",
            testProject);
        issueRepository.save(subIssue);

        // when
        SubIssueDetail parentIssueDto = issueFactory.createParentIssue(subIssue);

        // then
        assertThat(parentIssueDto).isNotNull();
        assertThat(parentIssueDto.getKey()).isEqualTo("TEST-6");
    }

    @Test
    @DisplayName("하위 이슈 목록 생성")
    void createChildIssueDtos() {
        // given
        // 부모 이슈 생성
        Issue parentIssue = Issue.createEpic(
            createIssueRequest(IssueType.EPIC),
            testMember,
            "TEST-6",
            testProject);
        issueRepository.save(parentIssue);

        // 자식 이슈 생성
        CreateIssue childRequest = createIssueRequest(IssueType.STORY);
        childRequest.setParentId(parentIssue.getId());

        Issue childIssue = Issue.createStory(
            childRequest,
            testMember,
            "TEST-7",
            testProject);
        issueRepository.save(childIssue);

        // when
        List<SubIssueDetail> childIssueDtos = issueFactory.createChildIssueDtos(parentIssue);

        // then
        assertThat(childIssueDtos).isNotEmpty();
        assertThat(childIssueDtos.size()).isEqualTo(1);
        assertThat(childIssueDtos.get(0).getKey()).isEqualTo("TEST-7");
    }

    private CreateIssue createIssueRequest(IssueType issueType) {
        return CreateIssue.builder()
            .title("테스트 제목")
            .content("테스트 내용")
            .type(issueType)
            .status(IssueStatus.DO)
            .label(IssueLabel.PLAN)
            .assigneeId(1L)
            .startDate(LocalDate.now())
            .endDate(LocalDate.now().plusDays(3))
            .build();
    }
}
