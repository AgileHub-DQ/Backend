package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.issue.domain.Story;
import dynamicquad.agilehub.issue.domain.Task;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.dto.IssueResponseDto;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.member.service.MemberService;
import dynamicquad.agilehub.project.domain.Project;
import java.util.List;
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

    @Transactional
    @Override
    public Long createIssue(IssueRequestDto.CreateIssue request, Project project) {
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
    public Long updateIssue(Issue issue, Project project, IssueRequestDto.EditIssue request) {

        Member assignee = memberService.findMember(request.getAssigneeId(), project.getId());

        Task task = Task.extractFromIssue(issue);
        Story upStory = retrieveStoryFromParentIssue(request.getParentId());
        task.updateTask(request, assignee, upStory);
        return task.getId();
    }

    @Override
    public IssueResponseDto.ContentDto createContentDto(Issue issue) {
        return IssueResponseDto.ContentDto.from(issue);
    }

    @Override
    public IssueResponseDto.IssueDetail createIssueDetail(Issue issue, IssueResponseDto.ContentDto contentDto,
                                                          AssigneeDto assigneeDto) {
        return IssueResponseDto.IssueDetail.from(issue, contentDto, assigneeDto, IssueType.TASK);
    }


    @Override
    public IssueResponseDto.SubIssueDetail createParentIssue(Issue issue) {
        Task task = Task.extractFromIssue(issue);
        Story story = task.getStory();
        if (story == null) {
            return null;
        }
        AssigneeDto assigneeDto = AssigneeDto.from(story);
        return IssueResponseDto.SubIssueDetail.from(story, IssueType.STORY, assigneeDto);
    }


    @Override
    public List<IssueResponseDto.SubIssueDetail> createChildIssueDtos(Issue issue) {
        return List.of();
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

    private Task toEntity(IssueRequestDto.CreateIssue request, Project project, int issueNumber, Member assignee,
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


}
