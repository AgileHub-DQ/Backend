package dynamicquad.agilehub.project.domain;

import dynamicquad.agilehub.member.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberProjectRepository extends JpaRepository<MemberProject, Long> {

    @Query("select mp.project from MemberProject mp where mp.member.id = :memberId")
    List<Project> findProjectsByMemberId(Long memberId);

    Optional<MemberProject> findByMemberIdAndProjectId(Long id, Long projectId);

    @Query("select mp.member from MemberProject mp where mp.project.id = :projectId")
    List<Member> findMembersByProjectId(Long projectId);
    
}
