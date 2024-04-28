package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProjectService {

    private final MemberProjectRepository memberProjectRepository;

    public void validateMemberInProject(AuthMember authMember, Long projectId) {
        Long memberId = authMember.getId();
        memberProjectRepository.findByMemberIdAndProjectId(memberId, projectId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_IN_PROJECT));
    }


    @Transactional
    public void createMemberProject(AuthMember authMember, Project project, MemberProjectRole role) {
        Member member = Member.createPojoByAuthMember(authMember.getId(), authMember.getName());

        MemberProject memberProject = MemberProject.builder()
            .member(member)
            .project(project)
            .role(role)
            .build();

        memberProjectRepository.save(memberProject);
    }

}
