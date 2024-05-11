package dynamicquad.agilehub.issue.repository;

import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.dto.backlog.EpicResponseDto;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EpicRepository extends JpaRepository<Epic, Long> {

    List<Epic> findByProject(Project project);


    @Query(value = "SELECT e.issue_id AS epicId, COUNT(DISTINCT s.issue_id) AS storiesCount, "
        + "SUM(CASE WHEN i.status = 'DO' THEN 1 ELSE 0 END) AS statusDo, "
        + "SUM(CASE WHEN i.status = 'PROGRESS' THEN 1 ELSE 0 END) AS statusProgress, "
        + "SUM(CASE WHEN i.status = 'DONE' THEN 1 ELSE 0 END) AS statusDone "
        + "FROM epic e "
        + "LEFT JOIN (SELECT issue_id, epic_id FROM story WHERE epic_id IS NOT NULL) s ON e.issue_id = s.epic_id "
        + "LEFT JOIN (SELECT issue_id, status from issue WHERE project_id = :projectId) i ON i.issue_id = s.issue_id "
        + "GROUP BY e.issue_id;", nativeQuery = true)
    List<EpicResponseDto.EpicStatistic> getEpicStatics(Long projectId);
}
