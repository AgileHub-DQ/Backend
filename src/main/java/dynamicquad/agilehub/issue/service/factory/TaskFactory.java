package dynamicquad.agilehub.issue.service.factory;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.domain.Task;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("TASK_FACTORY")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskFactory implements IssueFactory {

    private final IssueRepository issueRepository;
    private final MemberProjectRepository memberProjectRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public Long createIssue(IssueCreateRequest request, Project project) {
        int issueNumber = (int) (issueRepository.countByProjectKey(project.getKey()) + 1);
        Member assignee = findMember(request.getAssigneeId(), project.getId());

        Task task = Task.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .number(issueNumber)
            .status(request.getStatus())
            .assignee(assignee)
            .project(project)
            .build();

        issueRepository.save(task);

        return task.getId();
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