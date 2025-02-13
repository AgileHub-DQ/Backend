package dynamicquad.agilehub.dummy.bulk.repository;

import dynamicquad.agilehub.project.domain.Project;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ProjectBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Project> projects) {
        String sql = "INSERT INTO project (name,project_key,created_at,updated_at) "
            + "VALUES (?, ?,?,?)";
        jdbcTemplate.batchUpdate(sql, projects, projects.size(),
            (PreparedStatement ps, Project project) -> {
                ps.setString(1, project.getName());
                ps.setString(2, project.getKey());
                ps.setString(3, String.valueOf(LocalDateTime.now()));
                ps.setString(4, String.valueOf(LocalDateTime.now()));
            });
    }
}
