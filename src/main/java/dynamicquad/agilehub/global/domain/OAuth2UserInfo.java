package dynamicquad.agilehub.global.domain;

import java.util.Map;
import lombok.Getter;

@Getter
public class OAuth2UserInfo {

    private final Map<String, Object> attributes;

    private final String id;

    private final String nickname;

    private final String profileImage;

    private final boolean isExist;

    public OAuth2UserInfo(Map<String, Object> attributes, String id, String nickname, String profileImage,
                          boolean isExist) {
        this.attributes = attributes;
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.isExist = isExist;
    }
}
