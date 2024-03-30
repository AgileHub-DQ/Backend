package dynamicquad.agilehub.issue.domain.task;

import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStoryId(Long storyId);

    List<Task> findByProject(Project project);

    @Query("SELECT t FROM Task t WHERE t.story.id IN :storyIds")
    List<Task> findTasksByStoryIds(@Param("storyIds") List<Long> storyIds);

}
