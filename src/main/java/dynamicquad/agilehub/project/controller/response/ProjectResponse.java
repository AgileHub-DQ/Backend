package dynamicquad.agilehub.project.controller.response;

import dynamicquad.agilehub.project.domain.Project;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class ProjectResponse {
    private Long id;
    private String key;
    private String name;
    private LocalDateTime createdAt;

    public static ProjectResponse fromEntity(Project project) {
        return ProjectResponse.builder()
            .id(project.getId())
            .key(project.getKey())
            .name(project.getName())
            .createdAt(project.getCreatedAt())
            .build();
    }
}
