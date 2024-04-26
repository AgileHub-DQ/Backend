package dynamicquad.agilehub.dummy.bulkRepository;

import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.story.Story;
import dynamicquad.agilehub.issue.domain.task.Task;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
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
    public void saveEpicAll(List<Issue> issues, Long projectId, Long sprintId, Long memberId) {
        String sql = "INSERT INTO issue (content, issue_type, number, project_id,status, title, member_id) "
            + "VALUES (?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, issues, issues.size(),
            (PreparedStatement ps, Issue issue) -> {
                ps.setString(1, issue.getContent());
                ps.setString(2, "EPIC");
                ps.setString(3, String.valueOf(issue.getNumber()));
                ps.setLong(4, projectId);
                ps.setString(5, String.valueOf(issue.getStatus()));
                ps.setString(6, issue.getTitle());
                ps.setLong(7, memberId);
            });
    }

    @Transactional
    public void saveIssueEpicAll(List<Epic> issues) {
        String epicSql = "INSERT INTO epic (issue_id, start_date,end_date) "
            + "VALUES (?, ?,?)";

        AtomicLong index = new AtomicLong(1L);
        jdbcTemplate.batchUpdate(epicSql, issues, issues.size(),
            (PreparedStatement ps, Epic issue) -> {
                ps.setLong(1, index.get());
                ps.setString(2, String.valueOf(issue.getStartDate()));
                ps.setString(3, String.valueOf(issue.getEndDate()));
                index.getAndIncrement();
            });
    }

    @Transactional
    public void saveStoryAll(List<Issue> issues, Long projectId, Long sprintId, Long memberId) {
        String sql = "INSERT INTO issue (content, issue_type, number, project_id, status, title, member_id) "
            + "VALUES (?,?,?,?, ?,?,?)";
        jdbcTemplate.batchUpdate(sql, issues, issues.size(),
            (PreparedStatement ps, Issue issue) -> {
                ps.setString(1, issue.getContent());
                ps.setString(2, "STORY");
                ps.setString(3, String.valueOf(issue.getNumber()));
                ps.setLong(4, projectId);
                ps.setString(5, String.valueOf(issue.getStatus()));
                ps.setString(6, issue.getTitle());
                ps.setLong(7, memberId);
            });
    }

    @Transactional
    public void saveIssueStoryAll(List<Story> issues, Long epicId, Long id) {
        String storySql = "INSERT INTO story (issue_id, epic_id, start_date,end_date) "
            + "VALUES (?,?,?,?)";

        AtomicLong index = new AtomicLong(id);
        jdbcTemplate.batchUpdate(storySql, issues, issues.size(),
            (PreparedStatement ps, Story issue) -> {
                ps.setLong(1, index.get());
                ps.setLong(2, epicId);
                ps.setString(3, String.valueOf(issue.getStartDate()));
                ps.setString(4, String.valueOf(issue.getEndDate()));
                index.getAndIncrement();
            });
    }

    @Transactional
    public void saveTaskAll(List<Issue> issues, Long projectId, Long sprintId, Long memberId) {
        String sql = "INSERT INTO issue (content, issue_type, number, project_id, status, title, member_id) "
            + "VALUES (?,?,?,?,?,?,?)";
        jdbcTemplate.batchUpdate(sql, issues, issues.size(),
            (PreparedStatement ps, Issue issue) -> {
                ps.setString(1, issue.getContent());
                ps.setString(2, "TASK");
                ps.setString(3, String.valueOf(issue.getNumber()));
                ps.setLong(4, projectId);
                ps.setString(5, String.valueOf(issue.getStatus()));
                ps.setString(6, issue.getTitle());
                ps.setLong(7, memberId);
            });
    }

    @Transactional
    public void saveIssueTaskAll(List<Task> issues, Long storyId, Long id) {
        String taskSql = "INSERT INTO task (issue_id, story_id) "
            + "VALUES (?,?)";

        AtomicLong index = new AtomicLong(id);
        jdbcTemplate.batchUpdate(taskSql, issues, issues.size(),
            (PreparedStatement ps, Task issue) -> {
                ps.setLong(1, index.get());
                ps.setLong(2, storyId);
                index.getAndIncrement();
            });
    }
}
