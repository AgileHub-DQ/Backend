package dynamicquad.agilehub.global.mail.repository;

import dynamicquad.agilehub.global.mail.model.InviteCode;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteCodeRedisRepository extends CrudRepository<InviteCode, String> {

    Optional<InviteCode> findByEmail(String email);
}
