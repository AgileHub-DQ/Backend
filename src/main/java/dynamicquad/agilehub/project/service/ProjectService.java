package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.project.controller.request.ProjectCreateReq;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Transactional
    public String createProject(ProjectCreateReq request) {
        if (projectRepository.existsByKey(request.key())) {
            throw new GeneralException(ErrorStatus.PROJECT_DUPLICATE);
        }

        return projectRepository.save(request.toEntity()).getKey();
    }


}
