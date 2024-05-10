package dynamicquad.agilehub.comment.service;

import static dynamicquad.agilehub.comment.response.CommentResponse.CommentReadResponse;

import dynamicquad.agilehub.comment.domain.Comment;
import dynamicquad.agilehub.comment.domain.CommentRepository;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.service.IssueValidator;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService {

    private final ProjectQueryService projectQueryService;
    private final IssueValidator issueValidator;

    private final CommentRepository commentRepository;

    public List<CommentReadResponse> getComments(String key, Long issueId) {
        Project project = projectQueryService.findProject(key);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(project.getId(), issueId);

        List<Comment> issues = commentRepository.findByIssueOrderByCreatedAtAsc(issue);

        return issues.stream().map(CommentReadResponse::fromEntity).toList();
    }
}
