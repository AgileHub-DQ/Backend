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
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.epic.EpicRepository;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.story.StoryRepository;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.issue.domain.task.TaskRepository;
import dynamicquad.agilehub.issue.service.factory.IssueFactory;
import dynamicquad.agilehub.issue.service.factory.IssueFactoryProvider;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import java.util.ArrayList;
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
    private final EpicRepository epicRepository;
    private final StoryRepository storyRepository;
    private final TaskRepository taskRepository;


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

        return buildIssueHierarchy(project);
    }

    public List<IssueHierarchyResponse> buildIssueHierarchy(Project project) {
        List<IssueHierarchyResponse> IssueHierarchyResponses = new ArrayList<>();

        List<Epic> epics = epicRepository.findByProject(project);
        for (Epic epic : epics) {
            IssueHierarchyResponse epicDto = IssueHierarchyResponse.fromEntity(epic, project.getKey(), null);

            List<Story> stories = storyRepository.findByEpicId(epic.getId());
            for (Story story : stories) {
                IssueHierarchyResponse storyDto = IssueHierarchyResponse.fromEntity(story, project.getKey(),
                    epic.getId());
                epicDto.addChild(storyDto);

                List<Task> tasks = taskRepository.findByStoryId(story.getId());
                for (Task task : tasks) {
                    IssueHierarchyResponse taskDto = IssueHierarchyResponse.fromEntity(task, project.getKey(),
                        story.getId());
                    storyDto.addChild(taskDto);
                }
            }
            IssueHierarchyResponses.add(epicDto);
        }

        List<Story> stories = storyRepository.findByProject(project);
        for (Story story : stories) {
            if (story.getEpic() != null) {
                continue;
            }
            IssueHierarchyResponse storyDto = IssueHierarchyResponse.fromEntity(story, project.getKey(), null);

            List<Task> tasks = taskRepository.findByStoryId(story.getId());
            for (Task task : tasks) {
                IssueHierarchyResponse taskDto = IssueHierarchyResponse.fromEntity(task, project.getKey(),
                    story.getId());
                storyDto.addChild(taskDto);
            }
            IssueHierarchyResponses.add(storyDto);
        }

        List<Task> tasks = taskRepository.findByProject(project);
        for (Task task : tasks) {
            if (task.getStory() != null) {
                continue;
            }
            IssueHierarchyResponse taskDto = IssueHierarchyResponse.fromEntity(task, project.getKey(), null);
            IssueHierarchyResponses.add(taskDto);
        }

        return IssueHierarchyResponses;
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
