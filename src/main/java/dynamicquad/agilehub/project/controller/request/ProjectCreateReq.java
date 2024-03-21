package dynamicquad.agilehub.project.controller.request;

import dynamicquad.agilehub.project.domain.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@EqualsAndHashCode
public class ProjectCreateReq {

    @NotBlank(message = "프로젝트 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "키 값은 필수입니다.")
    @Size(message = "키는 최소 2글자 이상입니다.", min = 2)
    @Pattern(message = "영숫자만 가능합니다", regexp = "[A-Za-z0-9]+")
    private String key;

    @Builder
    private ProjectCreateReq(String name, String key) {
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