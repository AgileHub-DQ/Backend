package dynamicquad.agilehub.dummy.bulkRepository;

import dynamicquad.agilehub.member.domain.Member;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class MemberBulkRepository {
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Member> members) {
        String sql = "INSERT INTO member (name,status,created_at,updated_at) "
            + "VALUES (?, ?,?,?)";
        jdbcTemplate.batchUpdate(sql, members, members.size(),
            (PreparedStatement ps, Member member) -> {
                ps.setString(1, member.getName());
                ps.setString(2, String.valueOf(member.getStatus()));
                ps.setString(3, String.valueOf(LocalDateTime.now()));
                ps.setString(4, String.valueOf(LocalDateTime.now()));
            });
    }
}
