package dynamicquad.agilehub.sprint.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.sprint.domain.Sprint;
import dynamicquad.agilehub.sprint.domain.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SprintValidator {

    private final SprintRepository sprintRepository;

    public void validateSprintInProject(Long projectId, Long sprintId) {
        if (!sprintRepository.existsByProjectIdAndId(projectId, sprintId)) {
            throw new GeneralException(ErrorStatus.SPRINT_NOT_IN_PROJECT);
        }

    }

    public Sprint findSprint(Long sprintId) {
        return sprintRepository.findById(sprintId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.SPRINT_NOT_FOUND));
    }
}
