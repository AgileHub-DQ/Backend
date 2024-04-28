package dynamicquad.agilehub.sprint.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.service.IssueValidator;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.MemberProjectService;
import dynamicquad.agilehub.project.service.ProjectValidator;
import dynamicquad.agilehub.sprint.controller.request.SprintRequest.SprintCreateRequest;
import dynamicquad.agilehub.sprint.controller.response.SprintResponse.SprintCreateResponse;
import dynamicquad.agilehub.sprint.domain.Sprint;
import dynamicquad.agilehub.sprint.domain.SprintRepository;
import dynamicquad.agilehub.sprint.domain.SprintStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SprintService {

    private final ProjectValidator projectValidator;
    private final IssueValidator issueValidator;
    private final SprintValidator sprintValidator;

    private final MemberProjectService memberProjectService;

    private final SprintRepository sprintRepository;
    private final IssueRepository issueRepository;

    @Transactional
    public SprintCreateResponse createSprint(String key, SprintCreateRequest request, AuthMember authMember) {
        Project project = projectValidator.findProject(key);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());

        Sprint sprint = request.toEntity();
        sprint.setProject(project);

        Sprint saveSprint = sprintRepository.save(sprint);
        return SprintCreateResponse.fromEntity(saveSprint);
    }

    @Transactional
    public void assignIssueToSprint(String key, Long sprintId, Long issueId, AuthMember authMember) {
        if (issueValidator.getIssueType(issueId).equals(IssueType.EPIC)) {
            throw new GeneralException(ErrorStatus.INVALID_ISSUE_TYPE);
        }

        Long projectId = validateMemberInProject(key, authMember);

        Sprint sprint = sprintValidator.findSprint(sprintId);
        sprintValidator.validateSprintInProject(projectId, sprintId);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(projectId, issueId);

        issue.setSprint(sprint);
    }

    @Transactional
    public void removeIssueFromSprint(String key, Long sprintId, Long issueId, AuthMember authMember) {

        Long projectId = validateMemberInProject(key, authMember);
        sprintValidator.validateSprintInProject(projectId, sprintId);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(projectId, issueId);

        issue.setSprint(null);
    }

    @Transactional
    public void changeSprintStatus(String key, Long sprintId, SprintStatus status, AuthMember authMember) {

        Long projectId = validateMemberInProject(key, authMember);
        sprintValidator.validateSprintInProject(projectId, sprintId);
        Sprint sprint = sprintValidator.findSprint(sprintId);
        if (status != null) {
            sprint.setStatus(status);
        }
    }

    @Transactional
    public void deleteSprint(String key, Long sprintId, AuthMember authMember) {
        Long projectId = validateMemberInProject(key, authMember);
        sprintValidator.validateSprintInProject(projectId, sprintId);

        issueRepository.updateIssueSprintNull(sprintId);
        sprintRepository.deleteById(sprintId);
    }

    private Long validateMemberInProject(String key, AuthMember authMember) {
        Long projectId = projectValidator.findProjectId(key);
        memberProjectService.validateMemberInProject(authMember.getId(), projectId);
        return projectId;
    }
}
