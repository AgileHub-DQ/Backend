package dynamicquad.agilehub.project.controller.response;

import dynamicquad.agilehub.project.domain.Project;
import java.time.LocalDate;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class ProjectResponseDto {
    private Long id;
    private String key;
    private String name;
    private LocalDate createdAt;

    public static ProjectResponseDto fromEntity(Project project) {
        return ProjectResponseDto.builder()
                .id(project.getId())
                .key(project.getKey())
                .name(project.getName())
                .createdAt(project.getCreatedAt().toLocalDate())
                .build();
    }

}
