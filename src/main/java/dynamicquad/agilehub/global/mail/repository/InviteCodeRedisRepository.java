package dynamicquad.agilehub.global.mail.repository;

import dynamicquad.agilehub.global.mail.model.InviteRedisEntity;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteCodeRedisRepository extends CrudRepository<InviteRedisEntity, String> {

    Optional<InviteRedisEntity> findByInviteCode(String inviteCode);
}
