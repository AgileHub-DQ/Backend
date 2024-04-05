package dynamicquad.agilehub.issue.comment;

import static org.assertj.core.api.Assertions.assertThat;

import dynamicquad.agilehub.issue.comment.domain.Comment;
import dynamicquad.agilehub.issue.comment.response.CommentResponse.CommentCreateResponse;
import dynamicquad.agilehub.issue.comment.service.CommentService;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class CommentServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private CommentService commentService;

    @Test
    @Transactional
    void 멤버가_특정이슈에_코멘트를_작성하면_해당코멘트가_저장된다() {
        // given
        Project project1 = createProject("프로젝트1", "project12311");
        em.persist(project1);
        Epic epic1P1 = createEpic("에픽1", "에픽1 내용", project1);
        em.persist(epic1P1);

        Member member = Member.builder()
            .name("member1")
            .build();
        em.persist(member);

        // when
        CommentCreateResponse commentCreateResponse = commentService.createComment(project1.getKey(), epic1P1.getId(),
            member.getId(), "코멘트 내용");

        // then
        assertThat(commentCreateResponse).isNotNull();
        assertThat(commentCreateResponse).extracting("content").isEqualTo("코멘트 내용");
        assertThat(commentCreateResponse).extracting("issueId").isEqualTo(epic1P1.getId());
        assertThat(commentCreateResponse).extracting("writerId").isEqualTo(member.getId());
        System.out.println("작성한 시간: " + commentCreateResponse.getCreatedAt());

    }

    @Test
    @Transactional
    void 특정이슈의_코멘트를_수정하면_반영된다() {
        //given
        Project project1 = createProject("프로젝트1", "project12311");
        em.persist(project1);
        Epic epic1P1 = createEpic("에픽1", "에픽1 내용", project1);
        em.persist(epic1P1);

        Member member = Member.builder()
            .name("member1")
            .build();
        em.persist(member);

        Comment comment = Comment.builder()
            .content("코멘트 내용")
            .writer(member)
            .issue(epic1P1)
            .build();

        em.persist(comment);

        //when
        commentService.updateComment(project1.getKey(), epic1P1.getId(), comment.getId(), "수정된 코멘트 내용", member.getId());

        //then
        Comment findComment = em.find(Comment.class, comment.getId());
        assertThat(findComment.getContent()).isEqualTo("수정된 코멘트 내용");
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