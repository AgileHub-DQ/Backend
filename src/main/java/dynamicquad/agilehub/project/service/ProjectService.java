package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectCreateRequest;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectUpdateRequest;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;

    @Transactional
    public String createProject(ProjectCreateRequest request) {

        validateKeyUniqueness(request.getKey());

        //유저 확인후 유저 - 프로젝트 매핑하는 로직 필요

        return projectRepository.save(request.toEntity()).getKey();
    }

    @Transactional
    public String updateProject(String originKey, ProjectUpdateRequest request) {

        Project project = projectValidator.findProject(originKey);

        validateKeyUniqueness(request.getKey());

        return project.updateProject(request).getKey();

    }


    private void validateKeyUniqueness(String key) {
        if (projectRepository.existsByKey(key)) {
            throw new GeneralException(ErrorStatus.PROJECT_DUPLICATE);
        }
    }

}
