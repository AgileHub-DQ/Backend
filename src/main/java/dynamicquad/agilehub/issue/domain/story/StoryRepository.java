package dynamicquad.agilehub.issue.domain.story;

import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByEpicId(Long id);

    List<Story> findByProject(Project project);

    @Query("SELECT s FROM Story s WHERE s.epic.id IN :epicIds")
    List<Story> findStoriesByEpicIds(@Param("epicIds") List<Long> epicIds);

    List<Story> findStoriesByEpicId(Long epicId);
}
