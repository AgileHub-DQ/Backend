package dynamicquad.agilehub.dummy.bulk.repository;

import dynamicquad.agilehub.project.domain.MemberProject;
import jakarta.transaction.Transactional;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberProjectBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<MemberProject> memberProjects) {
        String sql = "INSERT INTO member_project (member_id,project_id,role) "
            + "VALUES (?, ?,?)";

        AtomicLong index = new AtomicLong(1L);
        jdbcTemplate.batchUpdate(sql, memberProjects, memberProjects.size(),
            (PreparedStatement ps, MemberProject memberProject) -> {
                ps.setLong(1, index.get());
                ps.setLong(2, index.get());
                ps.setString(3, String.valueOf(memberProject.getRole()));
                index.getAndIncrement();
            });
    }
}
