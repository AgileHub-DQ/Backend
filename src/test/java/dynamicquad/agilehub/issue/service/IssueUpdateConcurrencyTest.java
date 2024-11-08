package dynamicquad.agilehub.issue.service;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueLabel;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.issue.service.command.IssueService;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.member.repository.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class IssueUpdateConcurrencyTest {

    @Autowired
    private IssueService issueService;

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private MemberProjectRepository memberProjectRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private IssueRepository issueRepository;

    Project testProject;
    ArrayList<AuthMember> authMembers;

    @BeforeEach
    void init() {
        testProject = createTestProject();
        projectRepository.save(testProject);
        System.out.println(
            "잘 저장 = " + projectRepository.findByKey(testProject.getKey()).orElseThrow());
        authMembers = new ArrayList<>();
    }

    @Test
    @DisplayName("동시에 2명의 사용자가 하나의 이슈에 편집을 했을 때")
    void concurrentUpdateTest() {
        // given
        createMembers(2);
        Issue testIssue = createTestIssue();

        // when
        CompletableFuture<Void> userA = CompletableFuture.runAsync(() -> {
            issueService.updateIssue(testProject.getKey(), testIssue.getId(),
                createEditIssueByEditContent("사용자 A가 수정한 내용"), authMembers.get(0));
        }).exceptionally(e -> {
            Throwable cause = e.getCause();
            System.out.println("발생한 예외: " + cause.getClass().getName());
            System.out.println("예외 메시지: " + cause.getMessage());
            //e.printStackTrace();
            System.out.println("USER A 에러 발생");
            return null;
        });
        CompletableFuture<Void> userB = CompletableFuture.runAsync(() -> {

            issueService.updateIssue(testProject.getKey(), testIssue.getId(),
                createEditIssueByEditContent("사용자 B가 수정한 내용"), authMembers.get(1));

        }).exceptionally(e -> {
            Throwable cause = e.getCause();
            System.out.println("발생한 예외: " + cause.getClass().getName());
            System.out.println("예외 메시지: " + cause.getMessage());
            System.out.println("USER B 에러 발생");

            return null;
        });

        // 두 작업이 모두 완료될 때까지 대기
        CompletableFuture.allOf(userA, userB).join();

        // Then
        Issue updatedIssue = issueRepository.findById(testIssue.getId()).get();
        System.out.println("최종 내용: " + updatedIssue.getContent());
    }

    private void createMembers(int memberCount) {
        for (int j = 0; j < memberCount; j++) {
            Member member = Member.builder()
                .name("사용자" + j)
                .build();
            memberRepository.save(member);
            memberProjectRepository.save(MemberProject.builder()
                .member(member)
                .project(testProject)
                .role(MemberProjectRole.ADMIN)
                .build());
            authMembers.add(AuthMember.builder()
                .id(member.getId())
                .name(member.getName())
                .build());
        }
    }

    private Project createTestProject() {
        return Project.builder()
            .name("테스트 프로젝트")
            .key("TEST")
            .build();
    }


    private Issue createTestIssue() {
        Issue testIssue = Epic.builder()
            .title("테스트용 이슈")
            .content(
                """
                        <h1>테스트용 이슈</h1>
                        <p>이슈 내용입니다.</p>    
                    """
            )
            .number("")
            .status(IssueStatus.DO)
            .assignee(null)
            .label(IssueLabel.TEST)
            .project(testProject)
            .startDate(LocalDate.of(2024, 11, 6))
            .endDate(LocalDate.of(2024, 11, 9))
            .build();

        issueRepository.save(testIssue);
        return testIssue;
    }

    private IssueRequestDto.EditIssue createEditIssueByEditContent(String editContent) {
        return IssueRequestDto.EditIssue.builder()
            .title("테스트용 이슈")
            .content(editContent)
            .type(IssueType.EPIC)
            .status(IssueStatus.DO)
            .label(IssueLabel.TEST)
            .startDate(LocalDate.of(2024, 11, 6))
            .endDate(LocalDate.of(2024, 11, 9))
            .build();
    }
}
