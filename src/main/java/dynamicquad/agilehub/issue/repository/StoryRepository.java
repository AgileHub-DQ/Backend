package dynamicquad.agilehub.issue.repository;

import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByEpicId(Long id);

    List<Story> findByProject(Project project);

    List<Story> findStoriesByEpicId(Long epicId);
}
