package dynamicquad.agilehub.member.domain;

import dynamicquad.agilehub.global.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;


    @Column(nullable = false)
    private String name;

    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @Builder
    private Member(String name, String profileImageUrl, MemberStatus status) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
    }

}

