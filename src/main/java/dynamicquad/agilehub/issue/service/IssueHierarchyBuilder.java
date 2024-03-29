package dynamicquad.agilehub.issue.service;

import dynamicquad.agilehub.issue.controller.response.IssueHierarchyResponse;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.epic.EpicRepository;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.story.StoryRepository;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.issue.domain.task.TaskRepository;
import dynamicquad.agilehub.project.domain.Project;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueHierarchyBuilder {

    private final EpicRepository epicRepository;
    private final StoryRepository storyRepository;
    private final TaskRepository taskRepository;

    public List<IssueHierarchyResponse> buildAllIssuesHierarchy(Project project) {
        List<IssueHierarchyResponse> responses = new ArrayList<>();
        generateCompleteEpicRootStructure(project, responses);
        generateCompleteStoryRootStructure(project, responses);
        generateCompleteTaskRootStructure(project, responses);

        return responses;
    }

    private void generateCompleteEpicRootStructure(Project project, List<IssueHierarchyResponse> responses) {

        List<Epic> epics = epicRepository.findEpicsByProject(project);
        List<Story> storiesInEpics = groupStoriesByEpic(epics);
        List<Task> tasksInStories = groupTasksByStory(storiesInEpics);

        for (Epic epic : epics) {
            IssueHierarchyResponse epicDto = IssueHierarchyResponse.fromEntity(epic, project.getKey(), null);

            for (Story story : storiesInEpics) {
                if (!story.getEpic().equals(epic)) {
                    continue;
                }
                IssueHierarchyResponse storyDto = IssueHierarchyResponse.fromEntity(story, project.getKey(),
                    epic.getId());
                epicDto.addChild(storyDto);

                for (Task task : tasksInStories) {
                    if (!task.getStory().equals(story)) {
                        continue;
                    }
                    IssueHierarchyResponse taskDto = IssueHierarchyResponse.fromEntity(task, project.getKey(),
                        story.getId());
                    storyDto.addChild(taskDto);
                }
            }
            responses.add(epicDto);
        }
    }

    private void generateCompleteStoryRootStructure(Project project, List<IssueHierarchyResponse> responses) {

        List<Story> stories = storyRepository.findByProject(project);
        List<Task> tasksInStories = groupTasksByStory(stories);

        for (Story story : stories) {
            if (story.getEpic() != null) {
                continue;
            }
            IssueHierarchyResponse storyDto = IssueHierarchyResponse.fromEntity(story, project.getKey(), null);

            for (Task task : tasksInStories) {
                if (!task.getStory().equals(story)) {
                    continue;
                }
                IssueHierarchyResponse taskDto = IssueHierarchyResponse.fromEntity(task, project.getKey(),
                    story.getId());
                storyDto.addChild(taskDto);
            }
            responses.add(storyDto);
        }
    }

    private void generateCompleteTaskRootStructure(Project project, List<IssueHierarchyResponse> responses) {
        List<Task> tasks = taskRepository.findByProject(project);
        for (Task task : tasks) {
            if (task.getStory() != null) {
                continue;
            }
            IssueHierarchyResponse taskDto = IssueHierarchyResponse.fromEntity(task, project.getKey(), null);
            responses.add(taskDto);
        }
    }


    private List<Task> groupTasksByStory(List<Story> storiesInEpics) {
        List<Long> storyIds = storiesInEpics.stream().map(Story::getId).toList();
        return taskRepository.findTasksByStoryIds(storyIds);
    }

    private List<Story> groupStoriesByEpic(List<Epic> epics) {
        List<Long> epicIds = epics.stream().map(Epic::getId).toList();
        return storyRepository.findStoriesByEpicIds(epicIds);
    }
}
