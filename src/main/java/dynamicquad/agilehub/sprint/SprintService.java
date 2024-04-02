package dynamicquad.agilehub.sprint;

import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.service.ProjectValidator;
import dynamicquad.agilehub.sprint.controller.SprintRequest.SprintCreateRequest;
import dynamicquad.agilehub.sprint.controller.SprintResponse.SprintCreateResponse;
import dynamicquad.agilehub.sprint.domain.Sprint;
import dynamicquad.agilehub.sprint.domain.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SprintService {

    private final ProjectValidator projectValidator;

    private final SprintRepository sprintRepository;

    @Transactional
    public SprintCreateResponse createSprint(String key, SprintCreateRequest request) {
        Project project = projectValidator.findProject(key);

        Sprint sprint = request.toEntity();
        sprint.setProject(project);

        Sprint saveSprint = sprintRepository.save(sprint);
        return SprintCreateResponse.fromEntity(saveSprint);
    }
}
