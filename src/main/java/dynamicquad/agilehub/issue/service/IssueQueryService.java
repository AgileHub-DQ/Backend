package dynamicquad.agilehub.issue.service;

import static dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueReadResponseDto;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.EpicResponse;
import dynamicquad.agilehub.issue.controller.response.EpicResponse.EpicStatisticDto;
import dynamicquad.agilehub.issue.controller.response.EpicResponse.EpicWithStatisticResponse;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.controller.response.StoryResponse;
import dynamicquad.agilehub.issue.controller.response.TaskResponse;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.epic.EpicRepository;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.story.StoryRepository;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.issue.domain.task.TaskRepository;
import dynamicquad.agilehub.issue.service.factory.IssueFactory;
import dynamicquad.agilehub.issue.service.factory.IssueFactoryProvider;
import dynamicquad.agilehub.member.domain.Member;
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
    private final ProjectValidator projectValidator;

    private final EpicRepository epicRepository;
    private final StoryRepository storyRepository;
    private final TaskRepository taskRepository;


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


    public List<EpicWithStatisticResponse> getEpicsWithStats(String key) {
        Project project = projectValidator.findProject(key);
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]

        List<Epic> epicsByProject = epicRepository.findEpicsByProject(project);
        List<EpicResponse> epicResponses = getEpicResponses(epicsByProject, project);
        List<EpicStatisticDto> epicStatics = epicRepository.getEpicStatics(project.getId());

        return getEpicWithStatisticResponses(epicResponses, epicStatics);
    }

    public List<StoryResponse> getStoriesByEpic(String key, Long epicId) {
        Project project = projectValidator.findProject(key);
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]
        List<Story> storiesByEpic = storyRepository.findStoriesByEpicId(epicId);

        return storiesByEpic.stream()
            .map(story -> StoryResponse.fromEntity(story, project.getKey(), epicId))
            .toList();
    }

    public List<TaskResponse> getTasksByStory(String key, Long storyId) {
        Project project = projectValidator.findProject(key);
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]
        List<Task> tasksByStory = taskRepository.findTasksByStoryId(storyId);

        return tasksByStory.stream()
            .map(task -> TaskResponse.fromEntity(task, project.getKey(), storyId))
            .toList();
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

    private List<EpicWithStatisticResponse> getEpicWithStatisticResponses(List<EpicResponse> epicResponses,
                                                                          List<EpicStatisticDto> epicStatics) {
        return epicResponses.stream()
            .map(epic -> {
                EpicStatisticDto epicStatisticDto = epicStatics.stream()
                    .filter(epicStatistic -> epicStatistic.getEpicId().equals(epic.getId()))
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorStatus.EPIC_STATISTIC_NOT_FOUND));
                return new EpicWithStatisticResponse(epic, epicStatisticDto);
            })
            .toList();
    }

    private List<EpicResponse> getEpicResponses(List<Epic> epicsByProject, Project project) {
        return epicsByProject.stream()
            .map(epic -> {
                Member assignee = epic.getAssignee();
                if (assignee == null) {
                    return EpicResponse.fromEntity(epic, project.getKey(), new AssigneeDto());
                }
                AssigneeDto assigneeDto = AssigneeDto.builder()
                    .id(assignee.getId())
                    .name(assignee.getName())
                    .build();
                return EpicResponse.fromEntity(epic, project.getKey(), assigneeDto);

            })
            .toList();
    }

}
