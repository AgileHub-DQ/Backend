package dynamicquad.agilehub.issue.service;

import static dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueReadResponseDto;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueHierarchyResponse;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.service.factory.IssueFactory;
import dynamicquad.agilehub.issue.service.factory.IssueFactoryProvider;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueQueryService {

    private final IssueFactoryProvider issueFactoryProvider;
    private final ProjectRepository projectRepository;
    private final IssueRepository issueRepository;
    private final IssueHierarchyBuilder issueHierarchyBuilder;


    public IssueReadResponseDto getIssue(String key, Long issueId) {
        Project project = findProject(key);
        Issue issue = findIssue(issueId);
        validateIssueInProject(project, issue);

        IssueType issueType = getIssueType(issueId);
        IssueFactory issueFactory = issueFactoryProvider.getIssueFactory(issueType);

        ContentDto contentDto = issueFactory.createContentDto(issue);
        AssigneeDto assigneeDto = createAssigneeDto(issue);
        IssueDto issueDto = issueFactory.createIssueDto(issue, contentDto, assigneeDto);
        SubIssueDto parentIssueDto = issueFactory.createParentIssueDto(issue);
        List<SubIssueDto> childIssueDtos = issueFactory.createChildIssueDtos(issue);

        return IssueReadResponseDto.builder()
            .issue(issueDto)
            .parentIssue(parentIssueDto)
            .childIssues(childIssueDtos)
            .build();
    }

    public List<IssueHierarchyResponse> getIssues(String key) {
        Project project = findProject(key);

        return issueHierarchyBuilder.buildAllIssuesHierarchy(project);
    }

    private AssigneeDto createAssigneeDto(Issue issue) {

        if (issue.getAssignee() == null) {
            return new AssigneeDto();
        }

        return AssigneeDto.builder()
            .id(issue.getAssignee().getId())
            .name(issue.getAssignee().getName())
            .build();
    }

    private IssueType getIssueType(Long issueId) {
        String type = issueRepository.findIssueTypeById(issueId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND));
        return IssueType.valueOf(type);
    }

    private void validateIssueInProject(Project project, Issue issue) {
        if (!issue.getProject().getId().equals(project.getId())) {
            throw new GeneralException(ErrorStatus.ISSUE_NOT_IN_PROJECT);
        }
    }

    private Issue findIssue(Long issueId) {
        return issueRepository.findById(issueId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.ISSUE_NOT_FOUND));
    }

    private Project findProject(String key) {
        return projectRepository.findByKey(key)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));
    }


}
