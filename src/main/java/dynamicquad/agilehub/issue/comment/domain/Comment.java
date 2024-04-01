package dynamicquad.agilehub.issue.comment.domain;

import dynamicquad.agilehub.global.domain.BaseEntity;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.member.domain.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "comment")
@Entity
public class Comment extends BaseEntity {

    @Id
    @Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Builder
    private Comment(String content, Issue issue, Member writer) {
        this.content = content;
        this.writer = writer;
        this.issue = issue;
        if (issue != null) {
            issue.getComments().add(this);
        }
    }

    public Comment updateComment(String content) {
        this.content = content;
        return this;
    }
}
