package dynamicquad.agilehub.issue.service;

import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueEditRequest;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.service.factory.IssueFactoryProvider;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueService {

    private final ProjectValidator projectValidator;
    private final IssueFactoryProvider issueFactoryProvider;
    private final IssueValidator issueValidator;

    @Transactional
    public Long createIssue(String key, IssueCreateRequest request) {
        Project project = projectValidator.findProject(key);

        return issueFactoryProvider.getIssueFactory(request.getType())
            .createIssue(request, project);

    }

    @Transactional
    public void updateIssue(String key, Long issueId, IssueEditRequest request) {
        Project project = projectValidator.findProject(key);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(project, issue);
        issueValidator.validateIssueType(issue, request.getType());

        issueFactoryProvider.getIssueFactory(request.getType())
            .updateIssue(issue, project, request);
    }


}
