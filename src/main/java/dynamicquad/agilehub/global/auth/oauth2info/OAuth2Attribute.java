package dynamicquad.agilehub.global.auth.oauth2info;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OAuth2Attribute {
    KAKAO("kakao", attributes -> {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        return new OAuth2UserInfo(
                attributes,
                attributes.get("id").toString(),
                profile.get("nickname").toString(),
                profile.get("profile_image_url").toString(),
                false
        );
    });

    private final String registrationId;
    private final Function<Map<String, Object>, OAuth2UserInfo> of;

    public static OAuth2UserInfo getUserInfo(String providerId, Map<String, Object> attributes) {
        return Arrays.stream(values())
                .filter(provider -> provider.registrationId.equals(providerId))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.UNSUPPORTED_PROVIDER))
                .of.apply(attributes);
    }

    public static OAuth2Attribute of(String registrationId) {
        return Arrays.stream(values())
                .filter(provider -> provider.registrationId.equals(registrationId))
                .findFirst()
                .orElseThrow(() -> new GeneralException(ErrorStatus.UNSUPPORTED_PROVIDER));
    }

}
