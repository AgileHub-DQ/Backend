package dynamicquad.agilehub.sprint.service;

import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueRepository;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import dynamicquad.agilehub.sprint.controller.response.SprintResponse.IssueInSprintDto;
import dynamicquad.agilehub.sprint.controller.response.SprintResponse.SprintReadResponse;
import dynamicquad.agilehub.sprint.domain.Sprint;
import dynamicquad.agilehub.sprint.domain.SprintRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SprintQueryService {

    private final ProjectQueryService projectQueryService;

    private final SprintRepository sprintRepository;
    private final IssueRepository issueRepository;

    public List<SprintReadResponse> getSprints(String key) {
        Long projectId = projectQueryService.findProjectId(key);
        List<Sprint> sprints = sprintRepository.findAllByProjectId(projectId);

        return sprints.stream()
            .map(sprint -> {
                    List<IssueInSprintDto> issueInSprintDtos = issueRepository.findBySprint(sprint).stream()
                        .map(issue -> convertToIssueInSprintDto(issue, key))
                        .toList();
                    return SprintReadResponse.builder()
                        .sprintId(sprint.getId())
                        .title(sprint.getTitle())
                        .status(String.valueOf(sprint.getStatus()))
                        .description(sprint.getTargetDescription())
                        .startDate(sprint.getStartDate() == null ? "" : sprint.getStartDate().toString())
                        .endDate(sprint.getEndDate() == null ? "" : sprint.getEndDate().toString())
                        .issueCount((long) issueInSprintDtos.size())
                        .issues(issueInSprintDtos)
                        .build();
                }
            )
            .toList();
    }

    private IssueInSprintDto convertToIssueInSprintDto(Issue issue, String key) {
        AssigneeDto assigneeDto = Optional.ofNullable(issue.getAssignee())
            .map(assignee -> new AssigneeDto(assignee.getId(), assignee.getName(), assignee.getProfileImageUrl()))
            .orElse(new AssigneeDto());
        return IssueInSprintDto.fromEntity(issue, key, assigneeDto);
    }

}
