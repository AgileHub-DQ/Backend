package dynamicquad.agilehub.issue.controller.request;

import dynamicquad.agilehub.issue.domain.IssueStatus;
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

        @NotBlank(message = "제목은 필수입니다.")
        private String title;

        @NotNull(message = "이슈 타입은 필수입니다.")
        private IssueType type;

        @NotNull(message = "상태는 필수입니다.")
        private IssueStatus status;

        private String content;

        private List<MultipartFile> files;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        private Long assigneeId;
    }

}
