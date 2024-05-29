package dynamicquad.agilehub.issue.repository;

import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.project.domain.Project;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {
    List<Story> findByEpicId(Long id);

    List<Story> findByProject(Project project);

    List<Story> findStoriesByEpicId(Long epicId);

    @Query(value = "SELECT i.content FROM Story s inner join Issue i on s.id=i.id WHERE s.startDate <= :endDate AND s.endDate >= :startDate AND i.project.id=:projectId")
    List<String> findContentsByMonth(LocalDate endDate, LocalDate startDate, Long projectId);
}
