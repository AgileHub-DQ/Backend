package dynamicquad.agilehub.global.auth.oauth2info;

import java.util.Map;
import lombok.Getter;

@Getter
public class OAuth2UserInfo {

    private Map<String, Object> attributes;

    private String id;

    private String nickname;

    private String profileImage;

    private boolean isExist;

    public OAuth2UserInfo(Map<String, Object> attributes, String id, String nickname, String profileImage,
                          boolean isExist) {
        this.attributes = attributes;
        this.id = id;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.isExist = isExist;
    }

    public void setExist(boolean isExist) {
        this.isExist = isExist;
    }

}
