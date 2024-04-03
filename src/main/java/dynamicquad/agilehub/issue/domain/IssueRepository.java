package dynamicquad.agilehub.issue.domain;

import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.sprint.domain.Sprint;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("select count(*) from Issue i join i.project p where p.key = :key")
    Long countByProjectKey(String key);

    @Query(value = "select issue_type from issue i where i.issue_id = :id", nativeQuery = true)
    Optional<String> findIssueTypeById(@Param("id") Long id);

    List<Issue> findByProject(Project project);

    boolean existsByProjectIdAndId(Long projectId, Long issueId);


    List<Issue> findBySprint(Sprint sprint);

    @Modifying
    @Query("update Issue i set i.sprint = null where i.sprint.id = :sprintId")
    void updateIssueSprintNull(Long sprintId);
}
