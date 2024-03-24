package dynamicquad.agilehub.project.controller.request;

import dynamicquad.agilehub.project.domain.Project;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class ProjectRequest {

    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ProjectCreateRequest {
        @Schema(description = "프로젝트 이름", example = "프로젝트 이름")
        @NotBlank(message = "프로젝트 이름은 필수입니다.")
        private String name;

        @Schema(description = "프로젝트를 식별하기 위한 고유한 키", example = "NP123")
        @NotBlank(message = "키 값은 필수입니다.")
        @Size(message = "키는 최소 2글자 이상입니다.", min = 2)
        @Pattern(message = "영숫자만 가능합니다", regexp = "[A-Za-z0-9]+")
        private String key;

        @Builder
        private ProjectCreateRequest(String name, String key) {
            this.name = name;
            this.key = key;
        }

        public Project toEntity() {
            return Project.builder()
                .name(name)
                .key(key)
                .build();
        }
    }


    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ProjectUpdateRequest {

        @Schema(description = "프로젝트 이름", example = "프로젝트 이름")
        @NotBlank(message = "프로젝트 이름은 필수입니다.")
        private String name;

        @Schema(description = "프로젝트 키", example = "NP123")
        @NotBlank(message = "키 값은 필수입니다.")
        @Size(message = "키는 최소 2글자 이상입니다.", min = 2)
        @Pattern(message = "영숫자만 가능합니다", regexp = "[A-Za-z0-9]+")
        private String key;

        @Builder
        private ProjectUpdateRequest(String name, String key) {
            this.name = name;
            this.key = key;
        }
    }
}
