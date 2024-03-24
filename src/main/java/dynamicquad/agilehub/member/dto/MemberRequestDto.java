package dynamicquad.agilehub.member.dto;

import dynamicquad.agilehub.global.auth.oauth2info.OAuth2Attribute;
import lombok.Builder;
import lombok.Getter;

public class MemberRequestDto {

    private MemberRequestDto() {
    }

    @Getter
    @Builder
    public static class MemberCreate {
        private String name;

        private String profileImageUrl;
    }

    @Getter
    @Builder
    public static class SocialLoginCreate {
        private OAuth2Attribute provider;

        private String distinctId;

        private Long memberId;
    }

}
