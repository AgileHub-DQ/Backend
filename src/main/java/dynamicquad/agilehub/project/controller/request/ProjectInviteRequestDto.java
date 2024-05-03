package dynamicquad.agilehub.project.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public class ProjectInviteRequestDto {

    private ProjectInviteRequestDto() {
    }

    @Schema(description = "초대 메일 송신")
    @Getter
    @Builder
    public static class SendInviteMail {
        @Schema(description = "초대할 사람의 이메일 주소", example = "example@agilehub.com")
        @NotBlank
        String email;

        @Schema(description = "초대할 프로젝트 id", example = "1")
        @NotNull
        long projectId;
    }

    @Schema(description = "초대 메일 수신")
    @Getter
    @Builder
    public static class ReceiveInviteMail {
        @Schema(description = "메일로부터 온 인증번호")
        @NotBlank
        String inviteCode;
    }
}
