package dynamicquad.agilehub.member.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.SocialLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final SocialLoginQueryService socialLoginQueryService;

    public Member findBySocialProviderAndDistinctId(String provider, String distinctId) {
        Member member = socialLoginQueryService.findByProviderAndDistinctId(provider, distinctId)
                .map(SocialLogin::getMember)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));
        member.getName();
        return member;
    }

}
