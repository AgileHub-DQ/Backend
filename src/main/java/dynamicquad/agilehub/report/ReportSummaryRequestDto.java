package dynamicquad.agilehub.report;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

public class ReportSummaryRequestDto {
    private ReportSummaryRequestDto() {
    }

    @Getter
    @Builder
    public static class SummaryRequestMail {

        @Schema(description = "요약본을 받을 사람의 이메일 주소", example = "example@agilehub.com")
        @NotBlank
        private String email;

        @Schema(description = "요약할 프로젝트 id", example = "1")
        @NotNull
        private long projectId;

    }
}
