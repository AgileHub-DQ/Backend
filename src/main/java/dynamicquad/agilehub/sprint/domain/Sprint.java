package dynamicquad.agilehub.sprint.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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

    @Builder
    public Sprint(String title, String targetDescription, LocalDate startDate, LocalDate endDate,
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

}
