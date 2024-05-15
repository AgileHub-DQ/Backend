package dynamicquad.agilehub.sprint.service.query;

import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.repository.IssueRepository;
import dynamicquad.agilehub.member.dto.AssigneeDto;
import dynamicquad.agilehub.project.service.ProjectQueryService;
import dynamicquad.agilehub.sprint.domain.Sprint;
import dynamicquad.agilehub.sprint.dto.SprintResponseDto;
import dynamicquad.agilehub.sprint.repository.SprintRepository;
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

    public List<SprintResponseDto.SprintDetail> getSprints(String key) {
        Long projectId = projectQueryService.findProjectId(key);
        List<Sprint> sprints = sprintRepository.findAllByProjectId(projectId);

        return sprints.stream()
            .map(sprint -> {
                    List<SprintResponseDto.IssueDetailInSprint> issues = issueRepository.findBySprint(sprint).stream()
                        .map(issue -> convertToIssueInSprintDto(issue, key))
                        .toList();
                    return SprintResponseDto.SprintDetail.from(sprint, issues);
                }
            )
            .toList();
    }

    private SprintResponseDto.IssueDetailInSprint convertToIssueInSprintDto(Issue issue, String key) {
        AssigneeDto assigneeDto = Optional.ofNullable(issue.getAssignee())
            .map(assignee -> new AssigneeDto(assignee.getId(), assignee.getName(), assignee.getProfileImageUrl()))
            .orElse(new AssigneeDto());
        return SprintResponseDto.IssueDetailInSprint.from(issue, key, assigneeDto);
    }

}
