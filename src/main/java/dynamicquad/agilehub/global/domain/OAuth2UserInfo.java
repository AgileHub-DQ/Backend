package dynamicquad.agilehub.global.domain;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuth2UserInfo {
    private final Map<String, Object> attributes;
    private final String id;
    private final String nickname;
    private final String profileImage;
}
