package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;

public interface IssueFactory {
    Long createIssue(IssueRequestDto.CreateIssue request, Project project);

    IssueResponseDto.ContentDto createContentDto(Issue issue);

    IssueResponseDto.IssueDetail createIssueDetail(Issue issue, IssueResponseDto.ContentDto contentDto,
                                                   AssigneeDto assigneeDto);

    IssueResponseDto.SubIssueDetail createParentIssue(Issue issue);

    List<IssueResponseDto.SubIssueDetail> createChildIssueDtos(Issue issue);

    Long updateIssue(Issue issue, Project project, IssueRequestDto.EditIssue request);

}
