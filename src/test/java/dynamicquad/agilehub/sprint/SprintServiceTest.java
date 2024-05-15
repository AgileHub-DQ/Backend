package dynamicquad.agilehub.sprint;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.sprint.domain.Sprint;
import dynamicquad.agilehub.sprint.domain.SprintStatus;
import dynamicquad.agilehub.sprint.dto.SprintRequestDto;
import dynamicquad.agilehub.sprint.dto.SprintResponseDto;
import dynamicquad.agilehub.sprint.service.command.SprintService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class SprintServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SprintService sprintService;

    @Test
    @Transactional
    void 스프린트를_생성한다() {
        // given
        Project project = createProject("project12311", "P1");
        em.persist(project);

        SprintRequestDto.CreateSprint request = SprintRequestDto.CreateSprint.builder()
            .title("스프린트 제목")
            .description("스프린트 목표")
            .startDate(LocalDate.of(2021, 1, 1))
            .endDate(LocalDate.of(2021, 1, 2))
            .build();

        Member member = Member.builder()
            .name("member1")
            .build();
        em.persist(member);

        MemberProject memberProject = MemberProject.builder()
            .member(member)
            .project(project)
            .role(MemberProjectRole.ADMIN)
            .build();
        em.persist(memberProject);

        AuthMember authMember = AuthMember.builder()
            .id(member.getId())
            .build();

        // when
        SprintResponseDto.CreatedSprint resp = sprintService.createSprint(project.getKey(), request, authMember);
        em.flush();

        // then
        Sprint findSprint = em.createQuery("select s from Sprint s where s.id = :id", Sprint.class)
            .setParameter("id", resp.getSprintId())
            .getSingleResult();
        assertThat(findSprint.getTitle()).isEqualTo("스프린트 제목");
        assertThat(findSprint.getStatus()).isEqualTo(SprintStatus.PLANNED);
        assertThat(findSprint.getProject()).isEqualTo(project);

    }

    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }

}