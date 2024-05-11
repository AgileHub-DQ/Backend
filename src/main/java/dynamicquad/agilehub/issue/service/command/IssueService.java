package dynamicquad.agilehub.issue.service.command;

import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.issue.service.IssueValidator;
import dynamicquad.agilehub.issue.service.factory.IssueFactoryProvider;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.MemberProjectService;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class IssueService {

    private final ProjectQueryService projectQueryService;
    private final IssueFactoryProvider issueFactoryProvider;
    private final IssueValidator issueValidator;
    private final MemberProjectService memberProjectService;

    private final IssueRepository issueRepository;

    @Transactional
    public Long createIssue(String key, IssueRequestDto.CreateIssue request, AuthMember authMember) {

        Project project = validateMemberInProject(key, authMember);

        return issueFactoryProvider.getIssueFactory(request.getType())
            .createIssue(request, project);
    }


    @Transactional
    public void updateIssue(String key, Long issueId, IssueRequestDto.EditIssue request, AuthMember authMember) {

        Project project = validateMemberInProject(key, authMember);

        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(project.getId(), issueId);
        issueValidator.validateEqualsIssueType(issue, request.getType());

        issueFactoryProvider.getIssueFactory(request.getType())
            .updateIssue(issue, project, request);
    }


    @Transactional
    public void deleteIssue(String key, Long issueId, AuthMember authMember) {

        validateMemberInProject(key, authMember);

        Issue issue = issueValidator.findIssue(issueId);
        issueRepository.delete(issue);
    }

    @Transactional
    public void updateIssueStatus(String key, Long issueId, AuthMember authMember,
                                  IssueStatus updateStatus) {

        Project project = validateMemberInProject(key, authMember);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(project.getId(), issueId);

        issue.updateStatus(updateStatus);
    }

    private Project validateMemberInProject(String key, AuthMember authMember) {
        Project project = projectQueryService.findProject(key);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());
        return project;
    }
}
