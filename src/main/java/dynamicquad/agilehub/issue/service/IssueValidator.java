package dynamicquad.agilehub.issue.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueValidator {

    private final IssueRepository issueRepository;

    public void validateIssueInProject(Long projectId, Long issueId) {
        if (!issueRepository.existsByProjectIdAndId(projectId, issueId)) {
            throw new GeneralException(ErrorStatus.ISSUE_NOT_IN_PROJECT);
        }
    }

    public Issue findIssue(Long issueId) {
        return issueRepository.findById(issueId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.ISSUE_NOT_FOUND));
    }

    public void validateEqualsIssueType(Issue issue, IssueType type) {

        IssueType issueType = getIssueType(issue.getId());

        if (!issueType.equals(type)) {
            throw new GeneralException(ErrorStatus.ISSUE_TYPE_MISMATCH);
        }
    }

    public IssueType getIssueType(Long issueId) {
        String type = issueRepository.findIssueTypeById(issueId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND));
        return IssueType.valueOf(type);
    }
}
