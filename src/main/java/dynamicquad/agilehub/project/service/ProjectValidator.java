package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectValidator {

    private final ProjectRepository projectRepository;

    public Project findProject(String originKey) {
        return projectRepository.findByKey(originKey)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));
    }


}
