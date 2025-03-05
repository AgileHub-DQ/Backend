package dynamicquad.agilehub.issue.repository;

import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.dto.backlog.EpicResponseDto;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.sprint.domain.Sprint;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query(value = "select issue_type from issue_new i where i.issue_id = :id", nativeQuery = true)
    Optional<String> findIssueTypeById(@Param("id") Long id);

    boolean existsByProjectIdAndId(Long projectId, Long issueId);

    List<Issue> findBySprint(Sprint sprint);

    @Modifying(clearAutomatically = true)
    @Query("update Issue i set i.sprint = null where i.sprint.id = :sprintId")
    void updateIssueSprintNull(Long sprintId);

    List<Issue> findByParentIssueId(Long id);

    @Query("select i from Issue i where i.project = :project and i.issueType = :issueType")
    List<Issue> findEpicByProject(Project project, IssueType issueType);

    @Query(value = "SELECT i.issue_id AS epicId, "
            + "       COUNT(DISTINCT child.issue_id) AS storiesCount, "
            + "       SUM(CASE WHEN child.status = 'DO' THEN 1 ELSE 0 END) AS statusDo, "
            + "       SUM(CASE WHEN child.status = 'PROGRESS' THEN 1 ELSE 0 END) AS statusProgress, "
            + "       SUM(CASE WHEN child.status = 'DONE' THEN 1 ELSE 0 END) AS statusDone "
            + " FROM issue_new i "
            + " LEFT JOIN issue_new child "
            + "     ON i.issue_id = child.parent_issue_id "
            + " WHERE i.issue_type = 'EPIC' "
            + " AND i.project_id = :projectId "
            + " GROUP BY i.issue_id", nativeQuery = true)
    List<EpicResponseDto.EpicStatistic> getEpicStatics(Long projectId);

    @Query("SELECT i FROM Issue i WHERE i.parentIssueId = :epicId AND i.issueType = 'STORY'")
    List<Issue> findStoriesByEpicId(@Param("epicId") Long epicId);

    @Query("SELECT i FROM Issue i WHERE i.parentIssueId = :storyId AND i.issueType = 'TASK'")
    List<Issue> findTasksByStoryId(@Param("storyId") Long storyId);

    @Query("select i from Issue i where i.project = :project and i.issueType = 'EPIC'")
    List<Issue> findEpicsByProject(Project project);

    @Query("SELECT i FROM Issue i WHERE i.project = :project AND i.issueType = 'STORY'")
    List<Issue> findStoriesByProject(Project project);

    @Query("SELECT i FROM Issue i WHERE i.project = :project AND i.issueType = 'TASK'")
    List<Issue> findTasksByProject(Project project);

    @Query(value = "SELECT i.content FROM Issue i WHERE i.startDate <= :endDate AND i.endDate >= :startDate AND i.project.id=:projectId AND i.issueType = 'EPIC'")
    List<String> findEpicContentsByMonth(LocalDate startDate, LocalDate endDate, Long projectId);

    @Query(value = "SELECT i.content FROM Issue i WHERE i.startDate <= :endDate AND i.endDate >= :startDate AND i.project.id=:projectId AND i.issueType = 'STORY'")
    List<String> findStoryContentsByMonth(LocalDate startDate, LocalDate endDate, Long projectId);

    @Query(value = "SELECT i.content FROM Issue i WHERE i.startDate <= :endDate AND i.endDate >= :startDate AND i.project.id=:projectId AND i.issueType = 'TASK'")
    List<String> findTaskContentsByMonth(LocalDate startDate, LocalDate endDate, Long projectId);
}
