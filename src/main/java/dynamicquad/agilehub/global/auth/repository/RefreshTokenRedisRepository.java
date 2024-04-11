package dynamicquad.agilehub.global.auth.repository;

import dynamicquad.agilehub.global.auth.model.JwtRefreshToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<JwtRefreshToken, String> {

    Optional<JwtRefreshToken> findByRefreshToken(String refreshToken);
}
