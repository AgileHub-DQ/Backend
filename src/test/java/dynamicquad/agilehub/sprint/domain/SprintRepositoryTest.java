package dynamicquad.agilehub.sprint.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class SprintRepositoryTest {

    @Autowired
    private SprintRepository sprintRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @Transactional
    void 스프린트가_프로젝트_내에_존재하면_true() {
        // given
        Project project = Project.builder()
            .name("프로젝트1")
            .key("project21")
            .build();
        em.persist(project);

        Sprint sprint = Sprint.builder()
            .targetDescription("목표 설명")
            .build();
        em.persist(sprint);

        sprint.setProject(project);

        // when
        boolean exists = sprintRepository.existsByProjectIdAndId(project.getId(), sprint.getId());

        // then
        assertTrue(exists);
    }


}