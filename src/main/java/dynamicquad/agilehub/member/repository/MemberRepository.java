package dynamicquad.agilehub.member.repository;

import dynamicquad.agilehub.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
