package dynamicquad.agilehub.sprint.dto;

import dynamicquad.agilehub.sprint.domain.Sprint;
import dynamicquad.agilehub.sprint.domain.SprintStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

public class SprintRequestDto {
    private SprintRequestDto() {
    }

    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class CreateSprint {

        @Schema(description = "스프린트 제목", example = "스프린트 제목")
        @NotBlank(message = "스프린트 제목은 필수입니다.")
        private String title;

        @Schema(description = "스프린트 목표", example = "스프린트 목표")
        private String description;

        @Schema(description = "시작일", example = "2021-01-01")
        @NotNull(message = "시작일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startDate;

        @Schema(description = "종료일", example = "2021-01-02")
        @NotNull(message = "종료일은 필수입니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;

        @Builder
        public CreateSprint(String title, String description, LocalDate startDate, LocalDate endDate) {
            this.title = title;
            this.description = description;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Sprint toEntity() {
            return Sprint.builder()
                .title(title)
                .targetDescription(description)
                .status(SprintStatus.PLANNED)
                .startDate(startDate)
                .endDate(endDate)
                .build();
        }
    }

    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class AssignIssueToSprint {
        @NotNull(message = "이슈 ID는 필수입니다. 에픽 ID는 스프린트에 할당할 수 없습니다.")
        Long issueId;
    }

    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class SprintChangeStatus {
        SprintStatus status;
    }

}
