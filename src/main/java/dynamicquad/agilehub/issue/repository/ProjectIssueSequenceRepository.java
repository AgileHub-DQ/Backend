package dynamicquad.agilehub.issue.repository;

import dynamicquad.agilehub.issue.domain.ProjectIssueSequence;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface ProjectIssueSequenceRepository extends JpaRepository<ProjectIssueSequence, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ProjectIssueSequence> findByProjectKey(String projectKey);

}
