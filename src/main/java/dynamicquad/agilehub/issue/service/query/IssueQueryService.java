package dynamicquad.agilehub.issue.service.query;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.issue.domain.Task;
import dynamicquad.agilehub.issue.dto.IssueResponseDto;
import dynamicquad.agilehub.issue.dto.backlog.EpicResponseDto;
import dynamicquad.agilehub.issue.dto.backlog.StoryResponseDto;
import dynamicquad.agilehub.issue.dto.backlog.TaskResponseDto;
import dynamicquad.agilehub.issue.repository.EpicRepository;
import dynamicquad.agilehub.issue.repository.StoryRepository;
import dynamicquad.agilehub.issue.repository.TaskRepository;
import dynamicquad.agilehub.issue.service.IssueValidator;
import dynamicquad.agilehub.issue.service.factory.IssueFactory;
import dynamicquad.agilehub.issue.service.factory.IssueFactoryProvider;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.MemberProjectService;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class IssueQueryService {

    private final IssueFactoryProvider issueFactoryProvider;
    private final IssueValidator issueValidator;
    private final ProjectQueryService projectQueryService;
    private final MemberProjectService memberProjectService;

    private final EpicRepository epicRepository;
    private final StoryRepository storyRepository;
    private final TaskRepository taskRepository;


    public IssueResponseDto.IssueAndSubIssueDetail getIssue(String key, Long issueId, AuthMember authMember) {
        Long projectId = projectQueryService.findProjectId(key);
        memberProjectService.validateMemberInProject(authMember.getId(), projectId);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(projectId, issueId);

        IssueFactory issueFactory = issueFactoryProvider.getIssueFactory(issueValidator.getIssueType(issueId));

        IssueResponseDto.IssueDetail issueDetail = issueFactory.createIssueDetail(issue,
            issueFactory.createContentDto(issue), AssigneeDto.from(issue));
        IssueResponseDto.SubIssueDetail parentIssue = issueFactory.createParentIssue(issue);
        List<IssueResponseDto.SubIssueDetail> childIssueList = issueFactory.createChildIssueDtos(issue);

        return IssueResponseDto.IssueAndSubIssueDetail.from(issueDetail, parentIssue, childIssueList);
    }


    public List<EpicResponseDto.EpicDetailWithStatistic> getEpicsWithStats(String key, AuthMember authMember) {
        Project project = projectQueryService.findProject(key);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());

        List<Epic> epicsByProject = epicRepository.findByProject(project);
        List<EpicResponseDto.EpicDetailForBacklog> epicDetailForBacklogs = getEpicResponses(epicsByProject, project);
        List<EpicResponseDto.EpicStatistic> epicStatics = epicRepository.getEpicStatics(project.getId());

        return getEpicWithStatisticResponses(epicDetailForBacklogs, epicStatics);
    }

    public List<StoryResponseDto.StoryDetailForBacklog> getStoriesByEpic(String key, Long epicId,
                                                                         AuthMember authMember) {
        Project project = projectQueryService.findProject(key);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());
        List<Story> storiesByEpic = storyRepository.findStoriesByEpicId(epicId);

        return storiesByEpic.stream()
            .map(story -> {
                AssigneeDto assigneeDto = AssigneeDto.from(story);
                return StoryResponseDto.StoryDetailForBacklog.from(story, project.getKey(), epicId, assigneeDto);
            })
            .toList();
    }

    public List<TaskResponseDto.TaskDetailForBacklog> getTasksByStory(String key, Long storyId, AuthMember authMember) {
        Project project = projectQueryService.findProject(key);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());
        List<Task> tasksByStory = taskRepository.findByStoryId(storyId);

        return tasksByStory.stream()
            .map(task -> {
                AssigneeDto assigneeDto = AssigneeDto.from(task);
                return TaskResponseDto.TaskDetailForBacklog.from(task, project.getKey(), storyId, assigneeDto);
            })
            .toList();
    }

    public List<IssueResponseDto.ReadSimpleIssue> getEpics(String key, AuthMember authMember) {
        Project project = projectQueryService.findProject(key);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());
        List<Epic> epicsByProject = epicRepository.findByProject(project);

        return epicsByProject.stream()
            .map(epic -> {
                AssigneeDto assigneeDto = AssigneeDto.from(epic);
                return IssueResponseDto.ReadSimpleIssue.from(epic, project.getKey(), IssueType.EPIC, assigneeDto);
            })
            .toList();
    }


    public List<IssueResponseDto.ReadSimpleIssue> getStories(String key, AuthMember authMember) {
        Project project = projectQueryService.findProject(key);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());
        List<Story> storiesByProject = storyRepository.findByProject(project);

        return storiesByProject.stream()
            .map(story -> {
                AssigneeDto assigneeDto = AssigneeDto.from(story);
                return IssueResponseDto.ReadSimpleIssue.from(story, project.getKey(), IssueType.STORY, assigneeDto);
            })
            .toList();
    }

    public List<IssueResponseDto.ReadSimpleIssue> getTasks(String key, AuthMember authMember) {
        Project project = projectQueryService.findProject(key);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());
        List<Task> tasksByProject = taskRepository.findByProject(project);

        return tasksByProject.stream()
            .map(task -> {
                AssigneeDto assigneeDto = AssigneeDto.from(task);
                return IssueResponseDto.ReadSimpleIssue.from(task, project.getKey(), IssueType.TASK, assigneeDto);
            })
            .toList();
    }

    public MonthlyReportDto getIssuesForMonth(String yearMonth, Long projectId) {

        YearMonth ym = YearMonth.parse(yearMonth); // "2024-05"와 같은 형식의 문자열
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();

        List<String> contentsByEpic = epicRepository.findContentsByMonth(endDate, startDate, projectId);
        List<String> contentsByStory = storyRepository.findContentsByMonth(endDate, startDate, projectId);
        List<String> contentsByTask = taskRepository.findContentsByMonth(endDate, startDate, projectId);

        return new MonthlyReportDto(contentsByEpic, contentsByStory, contentsByTask);


    }


    private List<EpicResponseDto.EpicDetailWithStatistic> getEpicWithStatisticResponses(
        List<EpicResponseDto.EpicDetailForBacklog> epics,
        List<EpicResponseDto.EpicStatistic> epicStatistics) {
        return epics.stream()
            .map(epic -> {
                EpicResponseDto.EpicStatistic epicStatisticDto = epicStatistics.stream()
                    .filter(epicStatistic -> epicStatistic.getEpicId().equals(epic.getId()))
                    .findFirst()
                    .orElseThrow(() -> new GeneralException(ErrorStatus.EPIC_STATISTIC_NOT_FOUND));
                return new EpicResponseDto.EpicDetailWithStatistic(epic, epicStatisticDto);
            })
            .toList();
    }

    private List<EpicResponseDto.EpicDetailForBacklog> getEpicResponses(List<Epic> epicsByProject, Project project) {
        return epicsByProject.stream()
            .map(epic -> {
                AssigneeDto assignee = AssigneeDto.from(epic);
                return EpicResponseDto.EpicDetailForBacklog.from(epic, project.getKey(), assignee);
            })
            .toList();
    }


}
