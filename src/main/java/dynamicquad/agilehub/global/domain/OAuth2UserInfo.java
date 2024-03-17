package dynamicquad.agilehub.global.domain;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class OAuth2UserInfo {

    private Map<String, Object> attributes;

    private String id;

    private String nickname;

    private String profileImage;

    private String email;

    private boolean isExist;

}
