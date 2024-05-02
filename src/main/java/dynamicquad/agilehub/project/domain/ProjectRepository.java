package dynamicquad.agilehub.project.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Boolean existsByKey(String key);

    Optional<Project> findByKey(String key);

    @Query("SELECT p.id FROM Project p WHERE p.key = :key")
    Optional<Long> findIdByKey(String key);

}
