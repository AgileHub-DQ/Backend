package dynamicquad.agilehub.project.service;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.member.repository.MemberRepository;
import dynamicquad.agilehub.project.controller.response.ProjectResponseDto;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class ProjectQueryServiceTest {

    @Autowired
    private ProjectQueryService projectQueryService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberProjectRepository memberProjectRepository;

    @Test
    @Transactional
    void 멤버의_프로젝트_목록을_조회한다() {
        // given
        Project p1 = createProject("프로젝트1", "PK1");
        Project p2 = createProject("프로젝트2", "PK2");
        projectRepository.saveAll(List.of(p1, p2));
        Member m1 = createMember("홍길동");
        memberRepository.save(m1);
        MemberProject memberProject1 = createMemberProject(m1, p1, MemberProjectRole.ADMIN);
        MemberProject memberProject2 = createMemberProject(m1, p2, MemberProjectRole.ADMIN);
        memberProjectRepository.saveAll(List.of(memberProject1, memberProject2));

        // when
        List<ProjectResponseDto> projects = projectQueryService.getProjects(m1.getId());
        // then
        assertThat(projects).hasSize(2);
        assertThat(projects).extracting("key")
                .containsOnly("PK1", "PK2");
        assertThat(projects).extracting("name")
                .containsOnly("프로젝트1", "프로젝트2");
    }

    private Project createProject(String projectName, String projectKey) {
        return projectRepository.save(Project.builder()
                .name(projectName)
                .key(projectKey)
                .build());
    }

    private Member createMember(String name) {
        return memberRepository.save(Member.builder()
                .name(name)
                .status(MemberStatus.ACTIVE)
                .build());
    }

    private MemberProject createMemberProject(Member member, Project project, MemberProjectRole role) {
        return memberProjectRepository.save(MemberProject.builder()
                .member(member)
                .project(project)
                .role(role)
                .build());
    }


}
