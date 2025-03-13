package dynamicquad.agilehub.dummy.bulk.repository;

import dynamicquad.agilehub.issue.domain.Issue;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class IssueBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Issue> issues, Long projectId, Long sprintId, Long memberId) {
        String sql =
            "INSERT INTO issue_new (content, issue_type, number, project_id, status, title, member_id, created_at,updated_at) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, issues, issues.size(),
            (PreparedStatement ps, Issue issue) -> {
                ps.setString(1, issue.getContent());
                ps.setString(2, issue.getIssueType().name());
                ps.setString(3, issue.getNumber());
                ps.setLong(4, projectId);
                ps.setString(5, issue.getStatus().name());
                ps.setString(6, issue.getTitle());
                ps.setLong(7, memberId);

                // created_at 값을 적절히 처리
                LocalDateTime createdAt = issue.getCreatedAt();
                if (createdAt != null) {
                    ps.setTimestamp(8, Timestamp.valueOf(createdAt));
                }
                else {
                    // 현재 시간으로 설정
                    ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                }

                // updated_at 값을 적절히 처리
                LocalDateTime updatedAt = issue.getUpdatedAt();
                if (updatedAt != null) {
                    ps.setTimestamp(9, Timestamp.valueOf(updatedAt));
                }
                else {
                    // 현재 시간으로 설정
                    ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
                }
            });
    }
}