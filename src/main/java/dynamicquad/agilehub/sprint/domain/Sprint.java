package dynamicquad.agilehub.sprint.domain;

import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "sprint")
@Entity
public class Sprint {
    @Id
    @Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sprint_id")
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private SprintStatus status;

    private String targetDescription;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Builder
    private Sprint(String title, String targetDescription, LocalDate startDate, LocalDate endDate,
                   SprintStatus status) {
        this.title = title;
        this.targetDescription = targetDescription;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }


    public Sprint updateSprint(String title, String targetDescription, SprintStatus status, LocalDate startDate,
                               LocalDate endDate) {
        this.title = title;
        this.targetDescription = targetDescription;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setStatus(SprintStatus status) {
        this.status = status;
    }
}
