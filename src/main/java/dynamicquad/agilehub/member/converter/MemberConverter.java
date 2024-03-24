package dynamicquad.agilehub.member.converter;

import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.MemberStatus;
import dynamicquad.agilehub.member.domain.SocialLogin;
import dynamicquad.agilehub.member.dto.MemberRequestDto;

public class MemberConverter {

    private MemberConverter() {
    }

    public static Member toMember(MemberRequestDto.CreateMember dto) {
        return Member.builder()
                .name(dto.getName())
                .profileImageUrl(dto.getProfileImageUrl())
                .status(MemberStatus.ACTIVE)
                .build();
    }

    public static SocialLogin toSocialLogin(Member member, MemberRequestDto.CreateSocialLogin dto) {
        return SocialLogin.builder()
                .provider(dto.getProvider())
                .distinctId(dto.getDistinctId())
                .member(member)
                .build();
    }

}
