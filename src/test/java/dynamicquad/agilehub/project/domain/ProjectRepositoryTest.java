package dynamicquad.agilehub.project.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @Transactional
    void 중복된_키를_가진_프로젝트를_생성하려는경우_예외가_발생한다() {
        // given
        Project project1 = Project.builder()
            .name("프로젝트1")
            .key("project21")
            .build();
        projectRepository.save(project1);
        // when
        Project project2 = Project.builder()
            .name("프로젝트2")
            .key("project21")
            .build();

        // then
        assertThatThrownBy(() -> projectRepository.save(project2))
            .isInstanceOf(DataIntegrityViolationException.class);


    }

    @Test
    @Transactional
    void 키로_특정_프로젝트Id를_가져온다() {
        // given
        Project project = Project.builder()
            .name("프로젝트1")
            .key("project21")
            .build();
        projectRepository.save(project);
        // when
        Long projectId = projectRepository.findIdByKey("project21")
            .orElseThrow();
        // then
        assertThat(projectId).isEqualTo(project.getId());
    }

}