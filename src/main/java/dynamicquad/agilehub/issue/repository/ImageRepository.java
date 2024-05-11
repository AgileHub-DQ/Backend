package dynamicquad.agilehub.issue.repository;

import dynamicquad.agilehub.issue.domain.Image;
import dynamicquad.agilehub.issue.domain.Issue;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByIssue(Issue issue);

    @Transactional
    @Modifying
    @Query("delete from Image i where i.path in :paths")
    void deletePaths(@Param("paths") List<String> paths);

    @Modifying
    @Query("delete from Image i where i in :images")
    void deleteImages(@Param("images") List<Image> images);
}
