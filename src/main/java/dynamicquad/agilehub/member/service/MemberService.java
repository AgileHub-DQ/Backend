package dynamicquad.agilehub.member.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.converter.MemberConverter;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.dto.MemberRequestDto;
import dynamicquad.agilehub.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public Long save(MemberRequestDto.CreateMember dto) {
        Member member = MemberConverter.toMember(dto);
        return memberRepository.save(member).getId();
    }

    public Member findById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
    }

}
