package dynamicquad.agilehub.issue.service;

import static dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueReadResponseDto;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueHierarchyResponse;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.service.factory.IssueFactory;
import dynamicquad.agilehub.issue.service.factory.IssueFactoryProvider;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.ProjectValidator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IssueQueryService {

    private final IssueFactoryProvider issueFactoryProvider;
    private final IssueValidator issueValidator;
    private final IssueHierarchyBuilder issueHierarchyBuilder;
    private final ProjectValidator projectValidator;


    public IssueReadResponseDto getIssue(String key, Long issueId) {
        Long projectId = projectValidator.findProjectId(key);
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(projectId, issueId);

        IssueType issueType = issueValidator.getIssueType(issueId);
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
        Project project = projectValidator.findProject(key);
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]
        return issueHierarchyBuilder.buildAllIssuesHierarchy(project);
    }

    private AssigneeDto createAssigneeDto(Issue issue) {

        if (issue.getAssignee() == null) {
            return new AssigneeDto();
        }
        //TODO: AssigneeDto 클래스에 해당 부분 fromEntity 메서드로 만들기 [ ]
        return AssigneeDto.builder()
            .id(issue.getAssignee().getId())
            .name(issue.getAssignee().getName())
            .build();
    }

}
