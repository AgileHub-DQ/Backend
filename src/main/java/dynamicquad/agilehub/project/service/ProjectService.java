package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectCreateRequest;
import dynamicquad.agilehub.project.controller.request.ProjectRequest.ProjectUpdateRequest;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
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
    private final MemberProjectService memberProjectService;

    @Transactional
    public String createProject(ProjectCreateRequest request, AuthMember authMember) {
        validateKeyUniqueness(request.getKey());
        Project project = projectRepository.save(request.toEntity());
        // 프로젝트 생성자는 프로젝트에 대한 모든 권한을 가짐(ADMIN)
        memberProjectService.createMemberProject(authMember, project, MemberProjectRole.ADMIN);

        return project.getKey();
    }

    @Transactional
    public String updateProject(String originKey, ProjectUpdateRequest request, AuthMember authMember) {
        Project project = projectValidator.findProject(originKey);
        memberProjectService.validateMemberInProject(authMember.getId(), project.getId());
        memberProjectService.validateUpdateProjectAuth(authMember, project.getId());
        validateKeyUniqueness(request.getKey());

        return project.updateProject(request).getKey();
    }


    private void validateKeyUniqueness(String key) {
        if (projectRepository.existsByKey(key)) {
            throw new GeneralException(ErrorStatus.PROJECT_DUPLICATE);
        }
    }

}
