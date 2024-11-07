package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.service.MemberService;
import dynamicquad.agilehub.project.controller.response.ProjectResponseDto;
import dynamicquad.agilehub.project.domain.Project;
import dynamicquad.agilehub.project.domain.ProjectRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional(readOnly = true)
public class ProjectQueryService {

    private final ProjectRepository projectRepository;

    private final MemberService memberService;
    private final MemberProjectService memberProjectService;

    public List<ProjectResponseDto> getProjects(Long memberId) {
        memberService.validateMemberExist(memberId);
        List<Project> projects = memberProjectService.findProjects(memberId);

        return projects.stream().map(ProjectResponseDto::fromEntity).toList();
    }

    public Project findProject(String originKey) {
        log.info("findProject: {}", originKey);
        return projectRepository.findByKey(originKey)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));
    }

    public Long findProjectId(String originKey) {
        return projectRepository.findIdByKey(originKey)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));
    }

    public Project findProjectById(long projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));
    }

}
