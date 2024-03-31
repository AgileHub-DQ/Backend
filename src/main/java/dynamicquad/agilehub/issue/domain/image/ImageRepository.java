package dynamicquad.agilehub.issue.domain.image;

import dynamicquad.agilehub.issue.domain.epic.Epic;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByIssue(Epic epic);

    @Transactional
    @Modifying
    @Query("delete from Image i where i.path in :paths")
    void deletePaths(@Param("paths") List<String> paths);
}
