package dynamicquad.agilehub.project.controller.response;

import dynamicquad.agilehub.project.domain.Project;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ProjectRes(Long id, String key, String name, LocalDateTime createdAt) {

    public static ProjectRes fromEntity(Project project) {
        return ProjectRes.builder()
            .id(project.getId())
            .key(project.getKey())
            .name(project.getName())
            .createdAt(project.getCreatedAt())
            .build();
    }
}
