package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.domain.MemberRepository;
import dynamicquad.agilehub.project.controller.request.ProjectCreateReq;
import dynamicquad.agilehub.project.controller.request.ProjectUpdateReq;
import dynamicquad.agilehub.project.controller.response.ProjectRes;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
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
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final MemberRepository memberRepository;
    private final MemberProjectRepository memberProjectRepository;

    @Transactional
    public String createProject(ProjectCreateReq request) {

        validateKeyUniqueness(request.getKey());

        //유저 확인후 유저 - 프로젝트 매핑하는 로직 필요

        return projectRepository.save(request.toEntity()).getKey();
    }


    public List<ProjectRes> getProjects(Long memberId) {

        validateMemberExist(memberId);

        //member로 project 조회
        List<Project> projects = memberProjectRepository.findProjectsByMemberId(memberId);

        if (projects.isEmpty()) {
            throw new GeneralException(ErrorStatus.PROJECT_NOT_FOUND);
        }

        log.info("projects : {}", projects);

        return projects.stream().map(ProjectRes::fromEntity).toList();
    }

    @Transactional
    public String updateProject(String originKey, ProjectUpdateReq request) {

        Project project = projectRepository.findByKey(originKey)
            .orElseThrow(() -> new GeneralException(ErrorStatus.PROJECT_NOT_FOUND));

        validateKeyUniqueness(request.getKey());

        return project.updateProject(request).getKey();

    }

    private void validateKeyUniqueness(String key) {
        if (projectRepository.existsByKey(key)) {
            throw new GeneralException(ErrorStatus.PROJECT_DUPLICATE);
        }
    }

    private void validateMemberExist(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }
}
