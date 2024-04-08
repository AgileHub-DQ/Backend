package dynamicquad.agilehub.sprint.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.issue.service.IssueValidator;
import dynamicquad.agilehub.project.domain.Project;
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

    private final SprintRepository sprintRepository;
    private final IssueRepository issueRepository;

    @Transactional
    public SprintCreateResponse createSprint(String key, SprintCreateRequest request) {
        Project project = projectValidator.findProject(key);
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]

        Sprint sprint = request.toEntity();
        sprint.setProject(project);

        Sprint saveSprint = sprintRepository.save(sprint);
        return SprintCreateResponse.fromEntity(saveSprint);
    }

    @Transactional
    public void assignIssueToSprint(String key, Long sprintId, Long issueId) {
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]
        if (issueValidator.getIssueType(issueId).equals(IssueType.EPIC)) {
            throw new GeneralException(ErrorStatus.INVALID_ISSUE_TYPE);
        }

        Long projectId = projectValidator.findProjectId(key);
        Sprint sprint = sprintValidator.findSprint(sprintId);
        sprintValidator.validateSprintInProject(projectId, sprintId);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(projectId, issueId);

        issue.setSprint(sprint);
    }

    @Transactional
    public void removeIssueFromSprint(String key, Long sprintId, Long issueId) {
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]
        Long projectId = projectValidator.findProjectId(key);
        sprintValidator.validateSprintInProject(projectId, sprintId);
        Issue issue = issueValidator.findIssue(issueId);
        issueValidator.validateIssueInProject(projectId, issueId);

        issue.setSprint(null);
    }

    @Transactional
    public void changeSprintStatus(String key, Long sprintId, SprintStatus status) {
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]
        Long projectId = projectValidator.findProjectId(key);
        sprintValidator.validateSprintInProject(projectId, sprintId);

        Sprint sprint = sprintValidator.findSprint(sprintId);
        if (status != null) {
            sprint.setStatus(status);
        }
    }

    @Transactional
    public void deleteSprint(String key, Long sprintId) {
        // TODO: 프로젝트에 속하는 멤버인지 확인하는 로직 필요 [ ]
        Long projectId = projectValidator.findProjectId(key);
        sprintValidator.validateSprintInProject(projectId, sprintId);

        issueRepository.updateIssueSprintNull(sprintId);
        sprintRepository.deleteById(sprintId);
    }
}
