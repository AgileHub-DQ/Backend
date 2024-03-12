package dynamicquad.agilehub.project.domain;

import dynamicquad.agilehub.global.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "project")
@Entity
public class Project extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String name;

    @Column(name = "project_key", nullable = false, unique = true)
    private String key;

    @OneToMany(mappedBy = "project")
    private List<MemberProject> memberProjects = new ArrayList<>();

    @Builder
    private Project(String name, String key) {
        this.name = name;
        this.key = key;
    }

    public Project update(String name, String key) {
        this.name = name;
        this.key = key;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project project)) {
            return false;
        }
        return Objects.equals(name, project.name) && Objects.equals(key, project.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, key);
    }
}
