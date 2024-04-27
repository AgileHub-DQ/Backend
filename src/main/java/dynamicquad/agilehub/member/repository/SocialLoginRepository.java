package dynamicquad.agilehub.member.repository;

import dynamicquad.agilehub.global.auth.oauth2info.OAuth2Attribute;
import dynamicquad.agilehub.member.domain.SocialLogin;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    Optional<SocialLogin> findByProviderAndDistinctId(OAuth2Attribute provider, String distinctId);
}
