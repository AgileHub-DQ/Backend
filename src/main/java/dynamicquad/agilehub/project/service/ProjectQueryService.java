package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.repository.MemberRepository;
import dynamicquad.agilehub.project.controller.response.ProjectResponse;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.Project;
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
    private final MemberProjectRepository memberProjectRepository;
    private final MemberRepository memberRepository;

    public List<ProjectResponse> getProjects(Long memberId) {

        validateMemberExist(memberId);

        List<Project> projects = memberProjectRepository.findProjectsByMemberId(memberId);

        if (projects.isEmpty()) {
            throw new GeneralException(ErrorStatus.PROJECT_NOT_FOUND);
        }

        log.info("projects : {}", projects);

        return projects.stream().map(ProjectResponse::fromEntity).toList();
    }

    private void validateMemberExist(Long memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new GeneralException(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }

}
