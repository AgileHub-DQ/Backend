package dynamicquad.agilehub.sprint.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintRepository extends JpaRepository<Sprint, Long> {

    boolean existsByProjectIdAndId(Long projectId, Long sprintId);
}
