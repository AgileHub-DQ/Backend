package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueEditRequest;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.AssigneeDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.IssueDto;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.SubIssueDto;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
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
    private final MemberProjectRepository memberProjectRepository;
    private final MemberRepository memberRepository;

    private final String TASK = "TASK";

    @Transactional
    @Override
    public Long createIssue(IssueCreateRequest request, Project project) {
        int issueNumber = (int) (issueRepository.countByProjectKey(project.getKey()) + 1);
        Member assignee = findMember(request.getAssigneeId(), project.getId());
        Story upStory = retrieveStoryFromParentIssue(request.getParentId());

        Task task = Task.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .number(issueNumber)
            .status(request.getStatus())
            .assignee(assignee)
            .project(project)
            .story(upStory)
            .build();

        issueRepository.save(task);

        return task.getId();
    }

    @Override
    public Long updateIssue(Issue issue, Project project, IssueEditRequest request) {
        return null;
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

        AssigneeDto assigneeDto = Optional.ofNullable(story.getAssignee())
            .map(assignee -> AssigneeDto.builder()
                .id(assignee.getId())
                .name(assignee.getName())
                .build())
            .orElse(new AssigneeDto());

        return SubIssueDto.builder()
            .issueId(story.getId())
            .key(story.getProject().getKey() + "-" + story.getNumber())
            .status(String.valueOf(story.getStatus()))
            .type("STORY")
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
