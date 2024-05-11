package dynamicquad.agilehub.issue.repository;

import dynamicquad.agilehub.issue.domain.Task;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStoryId(Long storyId);

    List<Task> findByProject(Project project);
}
