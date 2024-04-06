package dynamicquad.agilehub.issue.domain.epic;

import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EpicRepository extends JpaRepository<Epic, Long> {

    List<Epic> findByProject(Project project);

    @Query("SELECT e FROM Epic e WHERE e.project = :project")
    List<Epic> findEpicsByProject(@Param("project") Project project);
}
