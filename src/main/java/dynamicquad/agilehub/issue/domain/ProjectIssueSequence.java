package dynamicquad.agilehub.issue.domain;

import dynamicquad.agilehub.global.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_issue_sequence")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectIssueSequence extends BaseEntity {
    @Id
    @Column(name = "project_key")
    private String projectKey;

    @Column(name = "last_number")
    private int lastNumber;

//    @Version
//    private Long version;

    public ProjectIssueSequence(String projectKey) {
        this.projectKey = projectKey;
        this.lastNumber = 0;
    }

    public void updateLastNumber(int lastNumber) {
        this.lastNumber = lastNumber;
    }

    public int getNextNumber() {
        return ++lastNumber;
    }

    public int decrement() {
        return lastNumber--;
    }
}
