package dynamicquad.agilehub.member.repository;

import dynamicquad.agilehub.member.domain.SocialLogin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialLoginRepository extends JpaRepository<SocialLogin, Long> {
    SocialLogin findByProviderAndDistinctCode(String provider, String distinctCode);
}
