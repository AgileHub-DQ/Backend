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
        memberProjectService.createMemberProject(authMember, project, MemberProjectRole.ADMIN);

        return project.getKey();
    }

    @Transactional
    public String updateProject(String originKey, ProjectUpdateRequest request) {
        Project project = projectValidator.findProject(originKey);
        //TODO: 해당 멤버가 프로젝트에 속해있는지 확인하는 로직 필요. [ ]
        //TODO: 프로젝트 수정 권한이 있는지 확인하는 로직 필요. [ ] -> 프로젝트 생성자(ADMIN)만 수정 가능
        validateKeyUniqueness(request.getKey());
        return project.updateProject(request).getKey();
    }


    private void validateKeyUniqueness(String key) {
        if (projectRepository.existsByKey(key)) {
            throw new GeneralException(ErrorStatus.PROJECT_DUPLICATE);
        }
    }

}
