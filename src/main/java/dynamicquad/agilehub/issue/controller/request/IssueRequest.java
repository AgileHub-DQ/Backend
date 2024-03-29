package dynamicquad.agilehub.issue.controller.request;

import dynamicquad.agilehub.issue.domain.IssueStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;


public class IssueRequest {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class IssueCreateRequest {

        @Schema(description = "이슈 제목", example = "이슈 제목")
        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @Schema(description = "이슈 타입", example = "EPIC")
        @NotNull(message = "이슈 타입은 필수입니다.")
        private IssueType type;

        @Schema(description = "이슈 상태", example = "DO")
        @NotNull(message = "상태는 필수입니다.")
        private IssueStatus status;

        @Schema(description = "이슈 내용", example = "이슈 내용")
        private String content;


        private List<MultipartFile> files;

        @Schema(description = "시작일", example = "2021-01-01")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @Schema(description = "종료일", example = "2021-01-02")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        @Schema(description = "담당자 ID", example = "1")
        private Long assigneeId;
        
        @Schema(description = "부모 이슈 ID", example = "1")
        private Long parentId;
    }

}
