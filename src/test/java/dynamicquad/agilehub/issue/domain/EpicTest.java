package dynamicquad.agilehub.issue.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class EpicTest {

    @PersistenceContext
    private EntityManager em;

    private Project project;
    private Member member1;
    private Member member2;

    @BeforeEach
    void setUp() {
        project = Project.builder()
            .name("프로젝트1")
            .key("PROJECT_1")
            .build();

        em.persist(project);

        member1 = Member.builder()
            .name("멤버1")
            .email("sads@naver.com")
            .profileImageUrl("https://naver.com")
            .status(MemberStatus.ACTIVE)
            .build();

        member2 = Member.builder()
            .name("멤버2")
            .email("sese@google.com")
            .build();

        em.persist(member1);
        em.persist(member2);

        MemberProject memberProject1 = MemberProject.builder()
            .member(member1)
            .project(project)
            .role(MemberProjectRole.ADMIN)
            .build();

        MemberProject memberProject2 = MemberProject.builder()
            .member(member2)
            .project(project)
            .role(MemberProjectRole.VIEWER)
            .build();

        em.persist(memberProject1);
        em.persist(memberProject2);


    }

    @Test
    void 에픽_생성() {
        // given

        Epic epic = Epic.builder()
            .title("에픽1")
            .content("에픽1 내용")
            .number(1)
            .status(IssueStatus.DO)
            .startDate(LocalDate.of(2024, 3, 1))
            .endDate(LocalDate.of(2024, 3, 31))
            .assignee(member1)
            .project(project)
            .build();

        // when
        em.persist(epic);
        Epic findEpic = em.find(Epic.class, epic.getId());
        Long projectId = findEpic.getProject().getId();

        // then
        assertThat(findEpic).isEqualTo(epic);
        assertThat(project.getId()).isEqualTo(projectId);
        assertThat(findEpic.getAssignee()).isEqualTo(member1);


    }


}