package dynamicquad.agilehub.project.controller.request;

import dynamicquad.agilehub.project.domain.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProjectCreateReq(@NotBlank(message = "프로젝트 이름은 필수입니다.") String name,
                               @NotBlank(message = "키 값은 필수입니다.")
                               @Size(message = "키는 최소 2글자 이상입니다.", min = 2)
                               @Pattern(message = "영숫자만 가능합니다", regexp = "[A-Za-z0-9]+") String key) {

    public Project toEntity() {
        return Project.builder()
            .name(name)
            .key(key)
            .build();
    }
}