package dynamicquad.agilehub.project.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import dynamicquad.agilehub.project.domain.MemberProjectRepository;
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

}
