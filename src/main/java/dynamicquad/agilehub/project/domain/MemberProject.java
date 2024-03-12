package dynamicquad.agilehub.project.domain;

import dynamicquad.agilehub.member.domain.Member;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "member_project")
@Entity
public class MemberProject {

    @Id
    @Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_project_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberProjectRole role;


    public static MemberProject createMemberProject(Member member, Project project, MemberProjectRole role) {
        MemberProject memberProject = new MemberProject();
        memberProject.member = member;
        memberProject.project = project;
        memberProject.role = role;

        // 연관관계 설정
        member.getMemberProjects().add(memberProject);
        project.getMemberProjects().add(memberProject);

        return memberProject;
    }

    public MemberProject updateRole(MemberProjectRole role) {
        this.role = role;
        return this;
    }

}
