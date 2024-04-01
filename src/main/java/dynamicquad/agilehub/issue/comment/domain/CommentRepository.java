package dynamicquad.agilehub.issue.comment.domain;

import dynamicquad.agilehub.issue.domain.Issue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByIssueOrderByCreatedAtAsc(Issue issue);
}
