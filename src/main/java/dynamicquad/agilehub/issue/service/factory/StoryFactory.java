package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.issue.domain.Task;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto.SubIssueDetail;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.issue.repository.TaskRepository;
import dynamicquad.agilehub.issue.service.command.ImageService;
import dynamicquad.agilehub.issue.service.command.IssueNumberGenerator;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.member.service.MemberService;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("STORY_FACTORY")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class StoryFactory implements IssueFactory {


    private final IssueRepository issueRepository;
    private final TaskRepository taskRepository;
    private final ImageService imageService;
    private final IssueNumberGenerator issueNumberGenerator;

    private final MemberService memberService;

    @Value("${aws.s3.workingDirectory.issue}")
    private String WORKING_DIRECTORY;

    @Transactional
    @Override
    public Long createIssue(IssueRequestDto.CreateIssue request, Project project) {

        // 이슈 번호 생성
        String issueNumber = issueNumberGenerator.generate(project.getKey());

        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());
        Epic upEpic = retrieveEpicFromParentIssue(request.getParentId());
        Story story = toEntity(request, project, issueNumber, assignee, upEpic);

        issueRepository.save(story);
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            imageService.saveImages(story, request.getFiles(), WORKING_DIRECTORY);
        }

        return story.getId();
    }

    @Override
    public Long updateIssue(Issue issue, Project project, IssueRequestDto.EditIssue request) {

        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());

        Story story = Story.extractFromIssue(issue);
        Epic upEpic = retrieveEpicFromParentIssue(request.getParentId());
        story.updateStory(request, assignee, upEpic);

        imageService.cleanupMismatchedImages(story, request.getImageUrls(), WORKING_DIRECTORY);
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            imageService.saveImages(story, request.getFiles(), WORKING_DIRECTORY);
        }
        return story.getId();
    }


    @Override
    public IssueResponseDto.ContentDto createContentDto(Issue issue) {
        return IssueResponseDto.ContentDto.from(issue);
    }

    @Override
    public IssueResponseDto.IssueDetail createIssueDetail(Issue issue, IssueResponseDto.ContentDto contentDto,
                                                          AssigneeDto assigneeDto) {
        return IssueResponseDto.IssueDetail.from(issue, contentDto, assigneeDto, IssueType.STORY);
    }

    @Override
    public IssueResponseDto.SubIssueDetail createParentIssue(Issue issue) {
        Story story = Story.extractFromIssue(issue);
        Epic epic = story.getEpic();

        if (epic == null) {
            return new IssueResponseDto.SubIssueDetail();
        }
        AssigneeDto assigneeDto = AssigneeDto.from(epic);

        return IssueResponseDto.SubIssueDetail.from(epic, IssueType.EPIC, assigneeDto);
    }

    @Override
    public List<IssueResponseDto.SubIssueDetail> createChildIssueDtos(Issue issue) {
        Story story = Story.extractFromIssue(issue);
        List<Task> tasks = taskRepository.findByStoryId(story.getId());
        if (tasks.isEmpty()) {
            return List.of();
        }

        return tasks.stream()
            .map(this::getTaskToSubIssue)
            .toList();
    }


    private SubIssueDetail getTaskToSubIssue(Task task) {
        AssigneeDto assigneeDto = AssigneeDto.from(task);
        return IssueResponseDto.SubIssueDetail.from(task, IssueType.TASK, assigneeDto);
    }

    public Epic retrieveEpicFromParentIssue(Long parentId) {
        if (parentId == null) {
            return null;
        }
        validateParentIssue(parentId);
        return (Epic) issueRepository.findById(parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PARENT_ISSUE_NOT_FOUND));

    }

    private void validateParentIssue(Long parentId) {
        String type = issueRepository.findIssueTypeById(parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PARENT_ISSUE_NOT_FOUND));

        if (!IssueType.EPIC.equals(IssueType.valueOf(type))) {
            throw new GeneralException(ErrorStatus.PARENT_ISSUE_NOT_EPIC);
        }
    }

    private Story toEntity(IssueRequestDto.CreateIssue request, Project project, String issueNumber, Member assignee,
                           Epic upEpic) {
        return Story.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .number(issueNumber)
            .status(request.getStatus())
            .label(request.getLabel())
            .assignee(assignee)
            .project(project)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .epic(upEpic)
            .build();
    }


}
