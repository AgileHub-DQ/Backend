package dynamicquad.agilehub.project.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectCreateRequest;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectUpdateRequest;
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
class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;


    @Test
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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
    @Transactional
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


}