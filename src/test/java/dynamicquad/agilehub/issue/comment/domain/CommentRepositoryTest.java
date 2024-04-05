package dynamicquad.agilehub.issue.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class CommentRepositoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @Transactional
    void 이슈에_대한_전체_댓글조회() {
        // given
        Project project1 = createProject("프로젝트1", "p1");
        em.persist(project1);
        Epic epic1P1 = createEpic("에픽1", "에픽1 내용", project1);
        em.persist(epic1P1);
        Member member1 = Member.builder()
            .name("member1")
            .build();
        em.persist(member1);

        Comment comment1 = Comment.builder()
            .content("코멘트 내용")
            .writer(member1)
            .issue(epic1P1)
            .build();
        em.persist(comment1);

        Comment comment2 = Comment.builder()
            .content("코멘트 내용3")
            .writer(member1)
            .issue(epic1P1)
            .build();
        em.persist(comment2);

        Comment comment3 = Comment.builder()
            .content("코멘트 내용2")
            .writer(member1)
            .issue(epic1P1)
            .build();
        em.persist(comment3);
        // when
        List<Comment> byIssue = commentRepository.findByIssueOrderByCreatedAtAsc(epic1P1);
        // then
        assertThat(byIssue).hasSize(3);
        assertThat(byIssue).extracting(Comment::getWriter).contains(member1);
        assertThat(byIssue).extracting(Comment::getContent).containsExactly("코멘트 내용", "코멘트 내용3", "코멘트 내용2");

    }

    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }

    private Epic createEpic(String title, String content, Project project) {
        return Epic.builder()
            .title(title)
            .content(content)
            .status(IssueStatus.DO)
            .project(project)
            .build();
    }


}