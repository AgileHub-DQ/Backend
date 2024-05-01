package dynamicquad.agilehub.project.repository;

import dynamicquad.agilehub.project.domain.InviteRedisEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRedisRepository extends CrudRepository<InviteRedisEntity, String> {

    Optional<InviteRedisEntity> findByInviteCode(String inviteCode);
}
