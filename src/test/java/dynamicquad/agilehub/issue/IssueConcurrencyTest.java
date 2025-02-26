package dynamicquad.agilehub.issue;


import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.ProjectIssueSequence;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.issue.repository.ProjectIssueSequenceRepository;
import dynamicquad.agilehub.issue.service.command.IssueService;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.member.repository.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class IssueConcurrencyTest {

    @Autowired
    private IssueService issueService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectIssueSequenceRepository projectIssueSequenceRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberProjectRepository memberProjectRepository;

    Project testProject;
    ArrayList<AuthMember> authMembers;

    @BeforeEach
    void init() {
        testProject = createTestProject();
        projectRepository.save(testProject);
        projectIssueSequenceRepository.save(new ProjectIssueSequence(testProject.getKey()));
        authMembers = new ArrayList<>();
    }

    @Test
    @DisplayName("동시에 10명의 사용자가 이슈를 생성할 때")
    void concurrentCreateTest() throws InterruptedException {
        //given

        int numberOfUsers = 10;
        createMembers(numberOfUsers);

        CountDownLatch latch = new CountDownLatch(numberOfUsers);
        List<CompletableFuture<Long>> futures = new ArrayList<>();

        // when

        for (int i = 0; i < numberOfUsers; i++) {
            Integer finalI = i;
            CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                try {
                    IssueRequestDto.CreateIssue request = createIssueRequest();
                    return issueService.createIssue(testProject.getKey(), request, authMembers.get(finalI));
                } finally {
                    latch.countDown();
                }
            }).exceptionally(e -> {
                Throwable cause = e.getCause();
                System.out.println("발생한 예외: " + cause.getClass().getName());
                System.out.println("예외 메시지: " + cause.getMessage());
                System.out.println("USER 에러 발생");
                return null;
            });
            futures.add(future);
        }

        // then
        latch.await(10, TimeUnit.SECONDS); // 최대 10초 대기

        // 모든 Future 완료 대기
        List<Long> issueIds = futures.stream()
            .map(CompletableFuture::join)
            .toList();

        // 생성된 이슈 검증
        List<Issue> issues = issueRepository.findAllById(issueIds);

        // 이슈 번호 중복 검사
        Set<String> issueNumbers = issues.stream()
            .map(Issue::getNumber)
            .collect(toSet());

        assertThat(issueNumbers).hasSize(numberOfUsers);
    }

    private IssueRequestDto.CreateIssue createIssueRequest() {
        return IssueRequestDto.CreateIssue.builder()
            .title("이슈 제목")
            .content("이슈 내용")
            .type(IssueType.EPIC)
            .build();
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

    @AfterEach
    void cleanUp() {
        issueRepository.deleteAll();
        memberProjectRepository.deleteAll();
        projectRepository.deleteAll();
        memberRepository.deleteAll();
        projectIssueSequenceRepository.deleteAll();


    }

}
