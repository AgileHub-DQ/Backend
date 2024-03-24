package dynamicquad.agilehub.member.dto;

import dynamicquad.agilehub.global.auth.oauth2info.OAuth2Attribute;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequestDto {

    private MemberRequestDto() {
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateMember {
        private String name;

        private String profileImageUrl;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateSocialLogin {
        private OAuth2Attribute provider;

        private String distinctId;

        private Long memberId;
    }

}
