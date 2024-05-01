package dynamicquad.agilehub.project.domain;

import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

/**
 * inviteCode 만료 : 5분
 */
@Getter
@RedisHash(value = "invite_code")
public class InviteRedisEntity implements Serializable {

    @Id
    private String id;

    @Indexed
    private String inviteCode;

    @TimeToLive
    private Long expiration;

    @Builder
    public InviteRedisEntity(String inviteCode) {
        this.inviteCode = inviteCode;
        this.expiration = (long) 60 * 5;
    }

}
