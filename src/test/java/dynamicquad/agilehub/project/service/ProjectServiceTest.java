package dynamicquad.agilehub.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectCreateRequest;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectUpdateRequest;
import dynamicquad.agilehub.project.controller.response.ProjectResponse;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberProjectRepository memberProjectRepository;

    @Test
    void 신규프로젝트를_등록한다_성공시_key_반환() {
        // given
        ProjectCreateRequest request = ProjectCreateRequest.builder()
            .name("프로젝트1")
            .key("PK1")
            .build();
        // then
        assertThat(projectService.createProject(request))
            .isEqualTo("PK1");
    }

    @Test
    void 중복된_키를_가진_프로젝트를_등록하면_에러발생() {
        // given
        ProjectCreateRequest request = ProjectCreateRequest.builder()
            .name("프로젝트1")
            .key("PK1")
            .build();
        // when
        projectService.createProject(request);
        // then
        assertThatThrownBy(() -> projectService.createProject(request))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
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
        List<ProjectResponse> projects = projectService.getProjects(m1.getId());
        // then
        assertThat(projects).hasSize(2);
        assertThat(projects).extracting("key")
            .containsOnly("PK1", "PK2");
        assertThat(projects).extracting("name")
            .containsOnly("프로젝트1", "프로젝트2");
    }

    @Test
    void 멤버가_속한_프로젝트가_없다면_에러발생() {
        // given
        Member m1 = createMember("홍길동");
        memberRepository.save(m1);
        // when
        // then
        assertThatThrownBy(() -> projectService.getProjects(m1.getId()))
            .isInstanceOf(GeneralException.class);
    }

    @Test
    void 기존키가_존재하지않은키일때_예외를_반환한다() {
        // given
        Project p1 = createProject("프로젝트1", "PK1");
        projectRepository.save(p1);
        //when
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
            .name("프로젝트1")
            .key("PK2")
            .build();
        //then
        assertThatThrownBy(() -> projectService.updateProject("pk13", request))
            .isInstanceOf(GeneralException.class)
            .extracting("status").isEqualTo(ErrorStatus.PROJECT_NOT_FOUND);
    }

    @Test
    void 변경하려는키가_이미존재하는키면_예외를_반환한다() {
        // given
        Project p1 = createProject("프로젝트1", "PK1");
        Project p2 = createProject("프로젝트2", "PK2");
        projectRepository.saveAll(List.of(p1, p2));
        //when
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
            .name("프로젝트1")
            .key("PK2")
            .build();
        //then
        assertThatThrownBy(() -> projectService.updateProject("PK1", request))
            .isInstanceOf(GeneralException.class)
            .extracting("status").isEqualTo(ErrorStatus.PROJECT_DUPLICATE);

    }

    @Test
    void 변경하려는키가_원래기존키와같으면_예외를_반환한다() {
        // given
        Project p1 = createProject("프로젝트1", "PK1");
        Project p2 = createProject("프로젝트2", "PK2");
        projectRepository.saveAll(List.of(p1, p2));
        //when
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
            .name("프로젝트1")
            .key("PK1")
            .build();
        //then
        assertThatThrownBy(() -> projectService.updateProject("PK1", request))
            .isInstanceOf(GeneralException.class)
            .extracting("status").isEqualTo(ErrorStatus.PROJECT_DUPLICATE);

    }

    @Test
    void 변경된키가_정상적으로_프로젝트에_반영() {
        // given
        Project p1 = createProject("프로젝트1", "PK1");
        projectRepository.save(p1);
        //when
        ProjectUpdateRequest request = ProjectUpdateRequest.builder()
            .name("프로젝트1")
            .key("PK2")
            .build();
        //then
        assertThat(projectService.updateProject("PK1", request))
            .isEqualTo("PK2");
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