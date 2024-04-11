package dynamicquad.agilehub.global.auth.model;

import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

/**
 * JwtRefreshToken 갱신 : 14일
 */
@Getter
@RedisHash(value = "jwt_token", timeToLive = 60 * 60 * 24 * 14)
public class JwtRefreshToken implements Serializable {

    @Id
    private String id;

    @Indexed
    private String accessToken;

    private String refreshToken;

    @TimeToLive
    private Long expiration;

    public JwtRefreshToken(String accessToken, String refreshToken) {
        new JwtRefreshToken(accessToken, refreshToken, (long) (60 * 60));
    }

    public JwtRefreshToken(String accessToken, String refreshToken, Long expiration) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}
