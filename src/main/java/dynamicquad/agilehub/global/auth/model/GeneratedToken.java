package dynamicquad.agilehub.global.auth.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GeneratedToken {

    private final String accessToken;
    private final String refreshToken;

    @Builder
    public GeneratedToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
