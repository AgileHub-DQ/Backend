package dynamicquad.agilehub.member.service;

import dynamicquad.agilehub.global.auth.oauth2info.OAuth2Attribute;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.member.domain.SocialLogin;
import dynamicquad.agilehub.member.repository.SocialLoginRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SocialLoginQueryService {

    private final SocialLoginRepository socialLoginRepository;

    public Optional<SocialLogin> findByProviderAndDistinctCode(String providerId, String distinctCode) {
        return socialLoginRepository.findByProviderAndDistinctId(OAuth2Attribute.of(providerId), distinctCode);
    }

}
