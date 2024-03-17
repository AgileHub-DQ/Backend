package dynamicquad.agilehub.member.domain;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakaoMemberInfo {
    private final Map<String, Object> attributes;
    private final String nickname;
    private final String profileImageUrl;
}
