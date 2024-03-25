package dynamicquad.agilehub.issue.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("select count(*) from Issue i join i.project p where p.key = :key")
    Long countByProjectKey(String key);
}
