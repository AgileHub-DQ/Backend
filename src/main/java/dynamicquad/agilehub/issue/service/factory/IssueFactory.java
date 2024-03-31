package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;

public interface IssueFactory {
    Long createIssue(IssueCreateRequest request, Project project);

    ContentDto createContentDto(Issue issue);

    IssueDto createIssueDto(Issue issue, ContentDto contentDto, AssigneeDto assigneeDto);

    SubIssueDto createParentIssueDto(Issue issue);

    List<SubIssueDto> createChildIssueDtos(Issue issue);

    Long updateIssue(Issue issue, Project project, IssueCreateRequest request);
}
