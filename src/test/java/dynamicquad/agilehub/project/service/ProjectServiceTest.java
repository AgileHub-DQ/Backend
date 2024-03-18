package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.project.controller.request.ProjectCreateReq;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
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

    @Test
    void 신규프로젝트를_등록한다_성공시_key_반환() {
        // given
        ProjectCreateReq request = ProjectCreateReq.builder()
            .name("프로젝트1")
            .key("PK1")
            .build();
        // then
        Assertions.assertThat(projectService.createProject(request))
            .isEqualTo("PK1");
        flush();
    }

    @Test
    void 중복된_키를_가진_프로젝트를_등록하면_에러발생() {
        // given
        ProjectCreateReq request = ProjectCreateReq.builder()
            .name("프로젝트1")
            .key("PK1")
            .build();
        // when
        projectService.createProject(request);
        // then
        Assertions.assertThatThrownBy(() -> projectService.createProject(request))
            .isInstanceOf(RuntimeException.class);
        flush();
    }

    private void flush() {
        projectRepository.flush();
    }


}