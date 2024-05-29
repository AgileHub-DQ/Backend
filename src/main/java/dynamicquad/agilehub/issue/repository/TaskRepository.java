package dynamicquad.agilehub.issue.repository;

import dynamicquad.agilehub.issue.domain.Task;
import dynamicquad.agilehub.project.domain.Project;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStoryId(Long storyId);

    List<Task> findByProject(Project project);

    @Query(value = "SELECT i.content FROM Task t inner join Issue i on t.id=i.id WHERE t.startDate <= :endDate AND t.endDate >= :startDate AND i.project.id=:projectId")
    List<String> findContentsByMonth(LocalDate endDate, LocalDate startDate, Long projectId);
}
