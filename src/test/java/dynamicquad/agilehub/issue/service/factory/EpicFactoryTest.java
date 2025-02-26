package dynamicquad.agilehub.issue.service.factory;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.config.RedisTestContainer;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueLabel;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.member.repository.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
@ExtendWith(RedisTestContainer.class)
class EpicFactoryTest {

    @Autowired
    private EpicFactory epicFactory;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberProjectRepository memberProjectRepository;

    private Project project;
    private Member member;
    private MemberProject memberProject;

    @BeforeEach
    void setUp() {
        project = Project.builder()
            .key("PT")
            .name("PROJECT_TEST")
            .build();

        projectRepository.save(project);

        member = Member.builder()
            .name("Test Member")
            .status(MemberStatus.ACTIVE)
            .profileImageUrl("www.test.com")
            .build();

        memberRepository.save(member);

        memberProject = MemberProject.builder()
            .member(member)
            .project(project)
            .role(MemberProjectRole.ADMIN)
            .build();
        memberProjectRepository.save(memberProject);
    }

    @Test
    @DisplayName("이미지가 없는 Epic 이슈를 생성한다")
    void createEpicIssueWithoutImages() {
        // given
        IssueRequestDto.CreateIssue request = IssueRequestDto.CreateIssue
            .builder()
            .title("테스트 에픽 이슈")
            .label(IssueLabel.TEST)
            .files(null)
            .startDate(LocalDate.of(2025, 2, 10))
            .endDate(LocalDate.of(2025, 2, 20))
            .parentId(null)
            .assigneeId(member.getId())
            .content("Test Epic Content")
            .build();

        // when
        Long epicId = epicFactory.createIssue(request, project);

        // then
        Issue savedEpic = issueRepository.findById(epicId)
            .orElseThrow(() -> new RuntimeException("Epic not found"));

        Assertions.assertAll(
            () -> assertThat(savedEpic.getTitle()).isEqualTo("테스트 에픽 이슈"),
            () -> assertThat(savedEpic.getContent()).isEqualTo("Test Epic Content"),
            () -> assertThat(((Epic) savedEpic).getStartDate()).isEqualTo(LocalDate.of(2025, 2, 10)),
            () -> assertThat(((Epic) savedEpic).getEndDate()).isEqualTo(LocalDate.of(2025, 2, 20)),
            () -> assertThat(savedEpic.getAssignee().getId()).isEqualTo(member.getId()),
            () -> assertThat(savedEpic.getProject().getId()).isEqualTo(project.getId()),
            () -> assertThat(savedEpic.getNumber()).startsWith(project.getKey() + "-")
        );
    }


}