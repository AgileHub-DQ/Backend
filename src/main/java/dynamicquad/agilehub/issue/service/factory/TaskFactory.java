package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueEditRequest;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.member.service.MemberService;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("TASK_FACTORY")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TaskFactory implements IssueFactory {

    private final IssueRepository issueRepository;
    private final MemberService memberService;

    private final String TASK = "TASK";
    private final String STORY = "STORY";

    @Transactional
    @Override
    public Long createIssue(IssueCreateRequest request, Project project) {
        // TODO: 이슈가 삭제되면 이슈 번호가 중복될 수 있음 1번,2번,3번 이슈 생성뒤 2번 삭제하면 4번 이슈 생성시 3번이 되어 중복 [ ]
        // TODO: 이슈 번호 생성 로직을 따로 만들기 - number 최대로 큰 숫자 + 1로 로직 변경 [ ]
        int issueNumber = (int) (issueRepository.countByProjectKey(project.getKey()) + 1);

        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());
        Story upStory = retrieveStoryFromParentIssue(request.getParentId());
        Task task = toEntity(request, project, issueNumber, assignee, upStory);

        issueRepository.save(task);
        return task.getId();
    }

    @Override
    public Long updateIssue(Issue issue, Project project, IssueEditRequest request) {

        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());

        Task task = getTask(issue);
        Story upStory = retrieveStoryFromParentIssue(request.getParentId());
        task.updateTask(request, assignee, upStory);
        return task.getId();
    }

    @Override
    public ContentDto createContentDto(Issue issue) {
        return ContentDto.builder()
            .text(issue.getContent())
            .build();
    }

    @Override
    public IssueDto createIssueDto(Issue issue, ContentDto contentDto, AssigneeDto assigneeDto) {
        Task task = getTask(issue);

        return IssueDto.builder()
            .issueId(task.getId())
            .key(task.getProject().getKey() + "-" + task.getNumber())
            .title(task.getTitle())
            .type(TASK)
            .status(String.valueOf(task.getStatus()))
            .label(String.valueOf(task.getLabel()))
            .startDate("")
            .endDate("")
            .content(contentDto)
            .assignee(assigneeDto)
            .build();
    }


    @Override
    public SubIssueDto createParentIssueDto(Issue issue) {
        Task task = getTask(issue);
        Story story = task.getStory();

        if (story == null) {
            return null;
        }

        AssigneeDto assigneeDto = createAssigneeDto(story);

        return SubIssueDto.builder()
            .issueId(story.getId())
            .key(story.getProject().getKey() + "-" + story.getNumber())
            .status(String.valueOf(story.getStatus()))
            .label(String.valueOf(story.getLabel()))
            .type(STORY)
            .title(story.getTitle())
            .assignee(assigneeDto)
            .build();
    }


    @Override
    public List<SubIssueDto> createChildIssueDtos(Issue issue) {
        return List.of();
    }


    private Task getTask(Issue issue) {
        if (!(issue instanceof Task task)) {
            log.error("issue is not instance of Task = {}", issue.getClass());
            throw new GeneralException(ErrorStatus.ISSUE_TYPE_NOT_FOUND);
        }
        return task;
    }

    private Story retrieveStoryFromParentIssue(Long parentId) {
        if (parentId == null) {
            return null;
        }
        validateParentIssue(parentId);
        return (Story) issueRepository.findById(parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PARENT_ISSUE_NOT_FOUND));
    }

    private void validateParentIssue(Long parentId) {
        String type = issueRepository.findIssueTypeById(parentId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PARENT_ISSUE_NOT_FOUND));

        if (!IssueType.STORY.equals(IssueType.valueOf(type))) {
            throw new GeneralException(ErrorStatus.PARENT_ISSUE_NOT_STORY);
        }

    }

    private Task toEntity(IssueCreateRequest request, Project project, int issueNumber, Member assignee,
                          Story upStory) {
        return Task.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .number(issueNumber)
            .status(request.getStatus())
            .label(request.getLabel())
            .assignee(assignee)
            .project(project)
            .story(upStory)
            .build();
    }

    private AssigneeDto createAssigneeDto(Story story) {
        return Optional.ofNullable(story.getAssignee())
            .map(assignee -> AssigneeDto.from(assignee.getId(), assignee.getName(), assignee.getProfileImageUrl()))
            .orElse(new AssigneeDto());
    }

}
