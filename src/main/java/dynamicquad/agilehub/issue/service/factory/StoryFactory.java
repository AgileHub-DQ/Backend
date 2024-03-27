package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.controller.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.domain.image.Image;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.issue.domain.task.TaskRepository;
import dynamicquad.agilehub.issue.service.ImageService;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.Project;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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

    private final MemberProjectRepository memberProjectRepository;
    private final MemberRepository memberRepository;
    private final IssueRepository issueRepository;
    private final TaskRepository taskRepository;

    private final ImageService imageService;

    @Value("${aws.s3.workingDirectory.issue}")
    private String WORKING_DIRECTORY;
    private String STORY = "STORY";

    @Transactional
    @Override
    public Long createIssue(IssueCreateRequest request, Project project) {

        int issueNumber = (int) (issueRepository.countByProjectKey(project.getKey()) + 1);
        Member assignee = findMember(request.getAssigneeId(), project.getId());
        Epic upEpic = retrieveEpicFromParentIssue(request.getParentId());

        Story story = Story.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .number(issueNumber)
            .status(request.getStatus())
            .assignee(assignee)
            .project(project)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .epic(upEpic)
            .build();

        issueRepository.save(story);

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
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
            .startDate(Optional.ofNullable(story.getStartDate()).map(LocalDate::toString).orElse("시작 날짜 미정"))
            .endDate(Optional.ofNullable(story.getEndDate()).map(LocalDate::toString).orElse("종료 날짜 미정"))
            .content(contentDto)
            .assignee(assigneeDto)
            .build();
    }

    @Override
    public SubIssueDto createParentIssueDto(Issue issue) {
        log.info("createParentIssueDto");
        Story story = getStory(issue);
        Epic epic = story.getEpic();

        if (epic == null) {
            return new SubIssueDto();
        }

        AssigneeDto assigneeDto = Optional.ofNullable(epic.getAssignee())
            .map(assignee -> AssigneeDto.builder()
                .id(assignee.getId())
                .name(assignee.getName())
                .build())
            .orElse(new AssigneeDto());

        return SubIssueDto.builder()
            .issueId(epic.getId())
            .key(epic.getProject().getKey() + "-" + epic.getNumber())
            .status(String.valueOf(epic.getStatus()))
            .type("EPIC")
            .title(epic.getTitle())
            .assignee(assigneeDto)
            .build();

    }

    @Override
    public List<SubIssueDto> createChildIssueDtos(Issue issue) {
        log.info("createChildIssueDtos= Story 하위이슈인 테스크들 가져오기");
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

        AssigneeDto assigneeDto = Optional.ofNullable(task.getAssignee())
            .map(assignee -> AssigneeDto.builder()
                .id(assignee.getId())
                .name(assignee.getName())
                .build())
            .orElse(new AssigneeDto());

        return SubIssueDto.builder()
            .issueId(task.getId())
            .key(task.getProject().getKey() + "-" + task.getNumber())
            .status(task.getStatus().toString())
            .type("TASK")
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


    private Member findMember(Long assigneeId, Long projectId) {

        if (assigneeId == null) {
            return null;
        }

        Member member = memberRepository.findById(assigneeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        memberProjectRepository.findByMemberIdAndProjectId(member.getId(), projectId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_IN_PROJECT));

        return member;
    }
}
