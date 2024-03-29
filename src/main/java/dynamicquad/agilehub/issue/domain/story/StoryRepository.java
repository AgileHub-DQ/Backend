package dynamicquad.agilehub.issue.domain.story;

import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByEpicId(Long id);

    List<Story> findByProject(Project project);
}
