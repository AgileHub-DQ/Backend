package dynamicquad.agilehub.global.auth.repository;

import dynamicquad.agilehub.global.auth.model.JwtRefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRedisRepository extends CrudRepository<JwtRefreshToken, String> {

    Optional<JwtRefreshToken> findByAccessToken(String accessToken);
}
