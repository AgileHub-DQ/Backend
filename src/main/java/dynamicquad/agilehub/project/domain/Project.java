package dynamicquad.agilehub.project.domain;

import dynamicquad.agilehub.global.domain.BaseEntity;
import dynamicquad.agilehub.project.controller.request.ProjectUpdateReq;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@Table(name = "project")
@Entity
public class Project extends BaseEntity {
    @Id
    @Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String name;

    @Column(name = "project_key", unique = true)
    private String key;

    @Builder
    private Project(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public Project updateProject(ProjectUpdateReq request) {
        this.name = request.getName();
        this.key = request.getKey();
        return this;
    }

}
