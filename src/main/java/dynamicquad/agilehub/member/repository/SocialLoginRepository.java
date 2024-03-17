package dynamicquad.agilehub.member.repository;

import dynamicquad.agilehub.global.domain.OAuth2Attribute;
import dynamicquad.agilehub.member.domain.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    SocialLogin findByProviderAndDistinctCode(OAuth2Attribute provider, String distinctCode);
}
