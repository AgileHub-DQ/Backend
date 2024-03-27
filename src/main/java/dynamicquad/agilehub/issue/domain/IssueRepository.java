package dynamicquad.agilehub.issue.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("select count(*) from Issue i join i.project p where p.key = :key")
    Long countByProjectKey(String key);

    // issue_type 가져오기
    @Query(value = "select issue_type from issue i where i.issue_id = :id", nativeQuery = true)
    Optional<String> findIssueTypeById(@Param("id") Long id);

}
