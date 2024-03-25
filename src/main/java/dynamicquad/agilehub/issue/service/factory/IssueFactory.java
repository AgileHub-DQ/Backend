package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.project.domain.Project;

public interface IssueFactory {
    Long createIssue(IssueCreateRequest request, Project project);
}
