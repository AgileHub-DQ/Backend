package dynamicquad.agilehub.sprint.repository;

import dynamicquad.agilehub.sprint.domain.Sprint;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SprintRepository extends JpaRepository<Sprint, Long> {

    boolean existsByProjectIdAndId(Long projectId, Long sprintId);

    List<Sprint> findAllByProjectId(Long projectId);
}
