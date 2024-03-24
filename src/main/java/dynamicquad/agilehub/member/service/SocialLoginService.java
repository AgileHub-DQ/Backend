package dynamicquad.agilehub.member.service;

import dynamicquad.agilehub.member.converter.MemberConverter;
import dynamicquad.agilehub.member.domain.SocialLogin;
import dynamicquad.agilehub.member.dto.MemberRequestDto;
import dynamicquad.agilehub.member.repository.SocialLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SocialLoginService {

    private final SocialLoginRepository socialLoginRepository;

    public Long save(SocialLogin socialLogin) {
        return socialLoginRepository.save(socialLogin).getId();
    }

}
