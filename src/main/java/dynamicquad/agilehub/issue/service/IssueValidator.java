package dynamicquad.agilehub.issue.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueValidator {

    private final IssueRepository issueRepository;

    public void validateIssueInProject(Project project, Issue issue) {
        if (!issue.getProject().getId().equals(project.getId())) {
            throw new GeneralException(ErrorStatus.ISSUE_NOT_IN_PROJECT);
        }
    }

    public Issue findIssue(Long issueId) {
        return issueRepository.findById(issueId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.ISSUE_NOT_FOUND));
    }

}
