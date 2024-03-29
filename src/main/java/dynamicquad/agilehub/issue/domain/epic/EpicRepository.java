package dynamicquad.agilehub.issue.domain.epic;

import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EpicRepository extends JpaRepository<Epic, Long> {
    List<Epic> findByProject(Project project);
}
