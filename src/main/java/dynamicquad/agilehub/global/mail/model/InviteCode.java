package dynamicquad.agilehub.global.mail.model;

import jakarta.persistence.Id;
import java.io.Serializable;
import lombok.Getter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

/**
 * inviteCode 만료 : 10분
 */
@Getter
@RedisHash(value = "invite_code")
public class InviteCode implements Serializable {

    @Id
    private String id;

    @Indexed
    private String email;

    private String inviteCode;

    @TimeToLive
    private Long expiration;

    public InviteCode(String email, String inviteCode) {
        this.email = email;
        this.inviteCode = inviteCode;
        this.expiration = (long) 60 * 10;
    }

}
