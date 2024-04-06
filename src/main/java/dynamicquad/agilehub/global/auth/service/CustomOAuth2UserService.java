package dynamicquad.agilehub.global.auth.service;

import dynamicquad.agilehub.global.auth.model.SecurityMember;
import dynamicquad.agilehub.global.auth.oauth2info.OAuth2Attribute;
import dynamicquad.agilehub.global.auth.oauth2info.OAuth2UserInfo;
import dynamicquad.agilehub.member.converter.MemberConverter;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.SocialLogin;
import dynamicquad.agilehub.member.dto.MemberRequestDto;
import dynamicquad.agilehub.member.service.MemberService;
import dynamicquad.agilehub.member.service.SocialLoginQueryService;
import dynamicquad.agilehub.member.service.SocialLoginService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SocialLoginQueryService socialLoginQueryService;

    private final MemberService memberService;
    private final SocialLoginService socialLoginService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo userInfo = OAuth2Attribute.generateUserInfo(registrationId, oAuth2User.getAttributes());
        Optional<SocialLogin> findSocialLogin = socialLoginQueryService.findByProviderAndDistinctId(
                registrationId, userInfo.getId());

        Member member;
        // member가 존재하면 update, 존재하지 않으면 save
        if (findSocialLogin.isPresent()) {
            member = findSocialLogin.get().getMember();
            member.update(userInfo.getNickname(), userInfo.getProfileImage());
        } else {
            member = saveMember(userInfo);
        }

        return new SecurityMember(member, userInfo);
    }

    private Member saveMember(OAuth2UserInfo userInfo) {
        MemberRequestDto.CreateMember memberDto = MemberRequestDto.CreateMember.builder()
                .name(userInfo.getNickname())
                .profileImageUrl(userInfo.getProfileImage())
                .build();
        Member savedMember = memberService.save(MemberConverter.toMember(memberDto));

        MemberRequestDto.CreateSocialLogin socialLoginDto = MemberRequestDto.CreateSocialLogin.builder()
                .distinctId(userInfo.getId())
                .provider(OAuth2Attribute.of(userInfo.getProvider()))
                .build();
        socialLoginService.save(MemberConverter.toSocialLogin(savedMember, socialLoginDto));

        return savedMember;
    }
}
