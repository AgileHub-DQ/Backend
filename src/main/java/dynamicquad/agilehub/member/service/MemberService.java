package dynamicquad.agilehub.member.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.repository.MemberRepository;
import dynamicquad.agilehub.project.service.MemberProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberProjectService memberProjectService;

    public Member save(Member member) {
        return memberRepository.save(member);
    }

    public Member findMember(Long assigneeId, Long projectId) {
        if (assigneeId == null) {
            return null;
        }
        Member member = memberRepository.findById(assigneeId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        memberProjectService.validateMemberInProject(member.getId(), projectId);

        return member;
    }

}
