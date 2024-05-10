package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.image.Image;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.issue.domain.task.TaskRepository;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.service.ImageService;
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

    private final MemberService memberService;

    @Value("${aws.s3.workingDirectory.issue}")
    private String WORKING_DIRECTORY;
    private String STORY = "STORY";
    private String EPIC = "EPIC";
    private String TASK = "TASK";

    @Transactional
    @Override
    public Long createIssue(IssueRequestDto.CreateIssue request, Project project) {
        // TODO: 이슈가 삭제되면 이슈 번호가 중복될 수 있음 1번,2번,3번 이슈 생성뒤 2번 삭제하면 4번 이슈 생성시 3번이 되어 중복 [ ]
        // TODO: 이슈 번호 생성 로직을 따로 만들기 - number 최대로 큰 숫자 + 1로 로직 변경 [ ]
        int issueNumber = (int) (issueRepository.countByProjectKey(project.getKey()) + 1);

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

        Story story = getStory(issue);
        Epic upEpic = retrieveEpicFromParentIssue(request.getParentId());
        story.updateStory(request, assignee, upEpic);

        imageService.cleanupMismatchedImages(story, request.getImageUrls(), WORKING_DIRECTORY);
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            log.info("uploading images");
            imageService.saveImages(story, request.getFiles(), WORKING_DIRECTORY);
        }
        return story.getId();
    }


    @Override
    public ContentDto createContentDto(Issue issue) {
        return ContentDto.builder()
            .text(issue.getContent())
            .imagesURLs(issue.getImages().stream().map(Image::getPath).toList())
            .build();
    }

    @Override
    public IssueDto createIssueDto(Issue issue, ContentDto contentDto, AssigneeDto assigneeDto) {
        Story story = getStory(issue);

        return IssueDto.builder()
            .issueId(story.getId())
            .key(story.getProject().getKey() + "-" + story.getNumber())
            .title(story.getTitle())
            .type(STORY)
            .status(String.valueOf(story.getStatus()))
            .label(String.valueOf(story.getLabel()))
            .startDate(story.getStartDate() == null ? "" : story.getStartDate().toString())
            .endDate(story.getEndDate() == null ? "" : story.getEndDate().toString())
            .content(contentDto)
            .assignee(assigneeDto)
            .build();
    }

    @Override
    public SubIssueDto createParentIssueDto(Issue issue) {
        Story story = getStory(issue);
        Epic epic = story.getEpic();

        if (epic == null) {
            return new SubIssueDto();
        }
        AssigneeDto assigneeDto = createAssigneeDto(epic);

        return SubIssueDto.builder()
            .issueId(epic.getId())
            .key(epic.getProject().getKey() + "-" + epic.getNumber())
            .status(String.valueOf(epic.getStatus()))
            .label(String.valueOf(epic.getLabel()))
            .type(EPIC)
            .title(epic.getTitle())
            .assignee(assigneeDto)
            .build();

    }

    @Override
    public List<SubIssueDto> createChildIssueDtos(Issue issue) {
        Story story = getStory(issue);
        List<Task> tasks = taskRepository.findByStoryId(story.getId());
        if (tasks.isEmpty()) {
            return List.of();
        }

        return tasks.stream()
            .map(this::getTaskToSubIssueDto)
            .toList();
    }


    private Story getStory(Issue issue) {
        if (!(issue instanceof Story story)) {
            log.error("issue is not instance of Story = {}", issue.getClass());
            throw new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND);
        }
        return story;
    }


    private SubIssueDto getTaskToSubIssueDto(Task task) {

        AssigneeDto assigneeDto = createAssigneeDto(task);

        return SubIssueDto.builder()
            .issueId(task.getId())
            .key(task.getProject().getKey() + "-" + task.getNumber())
            .status(String.valueOf(task.getStatus()))
            .label(String.valueOf(task.getLabel()))
            .type(TASK)
            .title(task.getTitle())
            .assignee(assigneeDto)
            .build();
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

    private Story toEntity(IssueRequestDto.CreateIssue request, Project project, int issueNumber, Member assignee,
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

    private AssigneeDto createAssigneeDto(Issue issue) {

        if (issue.getAssignee() == null) {
            return new AssigneeDto();
        }
        return AssigneeDto.from(issue.getAssignee().getId(), issue.getAssignee().getName(),
            issue.getAssignee().getProfileImageUrl());
    }


}
