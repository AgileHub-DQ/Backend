package dynamicquad.agilehub.issue.dto;

import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.IssueLabel;
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

public class IssueRequestDto {
    private IssueRequestDto() {
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class CreateIssue {
        @Schema(description = "이슈 제목", example = "이슈 제목")
        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @Schema(description = "이슈 타입", example = "EPIC")
        @NotNull(message = "이슈 타입은 필수입니다.")
        private IssueType type;

        @Schema(description = "이슈 상태", example = "DO")
        @NotNull(message = "상태는 필수입니다.")
        private IssueStatus status;

        @Schema(description = "이슈 라벨 (PLAN, DESIGN, DEVELOP, TEST, FEEDBACK)", example = "PLAN")
        private IssueLabel label;

        @Schema(description = "이슈 내용", example = "이슈 내용")
        private String content;

        @Schema(description = "업로드할 이미지 파일들. 지원 포맷: jpg, png 등. 파일 올리지 않을거면 체크 박스 해제하세요")
        private List<MultipartFile> files;

        @Schema(description = "시작일", example = "2021-01-01")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @Schema(description = "종료일", example = "2021-01-02")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        @Schema(description = "담당자 ID", example = "1")
        private Long assigneeId;

        @Schema(description = "부모 이슈 ID. 에픽은 없으니 빈칸으로 두세요", example = "1")
        private Long parentId;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @Builder
    public static class EditIssue {
        @Schema(description = "이슈 제목", example = "이슈 제목")
        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @Schema(description = "타입은 변경하지 못합니다 기존 등록했던 이슈 타입 그대로 사용하세요", example = "EPIC")
        @NotNull(message = "이슈 타입은 필수입니다.")
        private IssueType type;

        @Schema(description = "이슈 상태", example = "DO")
        @NotNull(message = "상태는 필수입니다.")
        private IssueStatus status;

        @Schema(description = "이슈 라벨 (PLAN, DESIGN, DEVELOP, TEST, FEEDBACK)", example = "PLAN, DESIGN, DEVELOP, TEST, FEEDBACK")
        private IssueLabel label;

        @Schema(description = "이슈 내용", example = "이슈 내용")
        private String content;

        @Schema(description = "기존에 저장했던 이미지 URLs https://image.agilehub.store/1.jpg 같은 형식입니다")
        private List<String> imageUrls;

        @Schema(description = "업로드할 이미지 파일들. 지원 포맷: jpg, png 등. 파일 올리지 않을거면 체크 박스 해제하세요")
        private List<MultipartFile> files;

        @Schema(description = "시작일", example = "2021-01-01")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @Schema(description = "종료일", example = "2021-01-02")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        @Schema(description = "새 담당자 ID", example = "1")
        private Long assigneeId;

        @Schema(description = "부모 이슈 ID. 에픽은 없으니 빈칸으로 두세요. 예를들어 스토리의 부모이슈인 에픽을 교체하면 스토리안에 있던 테스크들도 같이 이동됩니다", example = "1")
        private Long parentId;
    }

}
