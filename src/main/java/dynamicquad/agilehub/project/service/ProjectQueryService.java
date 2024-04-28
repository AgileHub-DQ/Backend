package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.member.service.MemberService;
import dynamicquad.agilehub.project.controller.response.ProjectResponse;
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

    private final MemberService memberService;
    private final MemberProjectService memberProjectService;

    public List<ProjectResponse> getProjects(Long memberId) {

        memberService.validateMemberExist(memberId);
        List<Project> projects = memberProjectService.findProjects(memberId);

        return projects.stream().map(ProjectResponse::fromEntity).toList();
    }


}
