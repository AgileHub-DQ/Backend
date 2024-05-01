package dynamicquad.agilehub.project.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

public class ProjectInviteRequestDto {

    private ProjectInviteRequestDto() {
    }

    @Getter
    @Builder
    @Schema
    public static class SendInviteMail {
        String email;
        long projectId;
    }
}
