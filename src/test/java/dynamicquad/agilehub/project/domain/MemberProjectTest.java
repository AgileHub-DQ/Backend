package dynamicquad.agilehub.project.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("local")
@Transactional
@SpringBootTest
class MemberProjectTest {

    @PersistenceContext
    private EntityManager em;

    @Test
    void 팀에_새로운_멤버_추가() {
        // given
        Project project = Project.builder()
            .name("프로젝트1")
            .key("PROJECT_1")
            .build();
        em.persist(project);

        Member member1 = Member.builder()
            .name("멤버1")
            .email("asdaa@naver.com")
            .profileImageUrl("https://naver.com")
            .status(MemberStatus.ACTIVE)
            .build();

        em.persist(member1);

        Member member2 = Member.builder()
            .name("멤버2")
            .email("mastera@naver.com")
            .profileImageUrl("https://naver.com")
            .status(MemberStatus.ACTIVE)
            .build();

        em.persist(member2);

        // when
        MemberProject.createMemberProject(member1, project, MemberProjectRole.ADMIN);
        MemberProject.createMemberProject(member2, project, MemberProjectRole.VIEWER);

        // then
        Project findProject = em.find(Project.class, project.getId());
        assertThat(findProject.getMemberProjects().size()).isEqualTo(2);

        member2.getMemberProjects().forEach(memberProject -> {
            assertThat(memberProject.getProject()).isEqualTo(findProject);
        });


    }

}