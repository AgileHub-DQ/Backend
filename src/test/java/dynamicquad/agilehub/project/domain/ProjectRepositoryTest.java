package dynamicquad.agilehub.project.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void 중복된_키를_가진_프로젝트를_생성하려는경우_예외가_발생한다() {
        // given
        Project project1 = Project.builder()
            .name("프로젝트1")
            .key("project2")
            .build();
        projectRepository.save(project1);
        // when
        Project project2 = Project.builder()
            .name("프로젝트2")
            .key("project2")
            .build();

        // then
        assertThatThrownBy(() -> projectRepository.save(project2))
            .isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    void 대소문자가_다른키를_가진_프로젝트는_다른프로젝트라_판단한다() {
        // given
        Project project1 = Project.builder()
            .name("프로젝트1")
            .key("project2")
            .build();
        projectRepository.save(project1);
        // when
        Project project2 = Project.builder()
            .name("프로젝트2")
            .key("PROJECT2")
            .build();
        // then
        projectRepository.save(project2);
    }

}