package dynamicquad.agilehub.issue.domain.epic;

import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long>, EpicStaticsRepository {

    List<Epic> findByProject(Project project);

    // TODO: 곧 삭제될 예정
    @Query("SELECT e FROM Epic e WHERE e.project = :project")
    List<Epic> findEpicsByProject(@Param("project") Project project);
}
