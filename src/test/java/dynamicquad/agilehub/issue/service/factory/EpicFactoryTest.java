package dynamicquad.agilehub.issue.service.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueEditRequest;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.controller.response.IssueResponse.ContentDto;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.image.Image;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class EpicFactoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private EpicFactory epicFactory;


    @Test
    @Transactional
    void 이미지없는_에픽이슈를_정상적으로_생성() {
        Project project1 = createProject("프로젝트1", "project1231231");
        em.persist(project1);
        Project project2 = createProject("프로젝트2", "project2");
        em.persist(project2);
        Member member1 = createMember("멤버1");
        em.persist(member1);
        Member member2 = createMember("멤버2");
        em.persist(member2);

        MemberProject memberProject1 = MemberProject.builder()
            .member(member1)
            .project(project1)
            .role(MemberProjectRole.ADMIN)
            .build();
        em.persist(memberProject1);

        MemberProject memberProject2 = MemberProject.builder()
            .member(member2)
            .project(project2)
            .role(MemberProjectRole.ADMIN)
            .build();
        em.persist(memberProject2);

        IssueCreateRequest request = IssueCreateRequest.builder()
            .title("이슈 제목")
            .type(IssueType.EPIC)
            .status(IssueStatus.DO)
            .content("content 내용")
            .files(null)
            .startDate(LocalDate.of(2024, 2, 19))
            .endDate(LocalDate.of(2024, 2, 23))
            .assigneeId(member1.getId())
            .build();

        //when
        Long issueId = epicFactory.createIssue(request, project1);

        Epic epic = em.createQuery("select e from Epic e where e.id = :issueId", Epic.class)
            .setParameter("issueId", issueId)
            .getSingleResult();

        //then
        assertThat(epic.getTitle()).isEqualTo("이슈 제목");
        assertThat(epic.getContent()).isEqualTo("content 내용");
        assertThat(epic.getNumber()).isEqualTo(1);
        assertThat(epic.getStatus()).isEqualTo(IssueStatus.DO);
        assertThat(epic.getStartDate()).isEqualTo(LocalDate.of(2024, 2, 19));
        assertThat(epic.getEndDate()).isEqualTo(LocalDate.of(2024, 2, 23));
        assertThat(epic.getAssignee().getId()).isEqualTo(member1.getId());
        assertThat(epic.getProject().getId()).isEqualTo(project1.getId());


    }

    @Test
    @Transactional
    void 이미지없는_에픽이슈에_넣은_assinee가_서비스에_존재하지않을때_예외처리() {
        //given
        Project project1 = createProject("프로젝트1", "project12312353561");
        em.persist(project1);
        Project project2 = createProject("프로젝트2", "project2");
        em.persist(project2);

        IssueCreateRequest request = IssueCreateRequest.builder()
            .title("이슈 제목")
            .type(IssueType.EPIC)
            .status(IssueStatus.DO)
            .content("content 내용")
            .files(null)
            .startDate(LocalDate.of(2024, 2, 19))
            .endDate(LocalDate.of(2024, 2, 23))
            .assigneeId(10L)
            .build();

        //when
        //then
        assertThatThrownBy(() -> epicFactory.createIssue(request, project1)
        ).isInstanceOf(GeneralException.class).extracting("status").isEqualTo(ErrorStatus.MEMBER_NOT_FOUND);

    }

    @Test
    @Transactional
    void 이미지없는_에픽이슈에_넣은_assignee가_프로젝트에_속하지않을때_예외처리() {
        //given
        Project project1 = createProject("프로젝트1", "project11124");
        em.persist(project1);
        Project project2 = createProject("프로젝트2", "project2");
        em.persist(project2);

        Member member1 = createMember("멤버1");
        em.persist(member1);

        MemberProject memberProject1 = MemberProject.builder()
            .member(member1)
            .project(project1)
            .role(MemberProjectRole.ADMIN)
            .build();
        em.persist(memberProject1);

        IssueCreateRequest request = IssueCreateRequest.builder()
            .title("이슈 제목")
            .type(IssueType.EPIC)
            .status(IssueStatus.DO)
            .content("content 내용")
            .files(null)
            .startDate(LocalDate.of(2024, 2, 19))
            .endDate(LocalDate.of(2024, 2, 23))
            .assigneeId(member1.getId())
            .build();

        //when
        //then
        assertThatThrownBy(() -> epicFactory.createIssue(request, project2)
        ).isInstanceOf(GeneralException.class).extracting("status").isEqualTo(ErrorStatus.MEMBER_NOT_IN_PROJECT);

    }


    @Test
    @Transactional
    void 이슈가_가지고있는_이미지들_ContentDto로_반환() {
        //given
        Project project1 = createProject("프로젝트1", "project152626");
        em.persist(project1);

        Epic epic = Epic.builder()
            .title("이슈 제목")
            .content("content 내용")
            .number(1)
            .status(IssueStatus.DO)
            .assignee(null)
            .project(project1)
            .startDate(LocalDate.of(2024, 2, 19))
            .endDate(LocalDate.of(2024, 2, 23))
            .build();
        em.persist(epic);

        Image image1 = Image.builder()
            .path("https://file.jpg")
            .build();
        image1.setIssue(epic);
        em.persist(image1);

        Image image2 = Image.builder()
            .path("https://file2.jpg")
            .build();
        image2.setIssue(epic);
        em.persist(image2);

        //when
        ContentDto contentDto = epicFactory.createContentDto(epic);

        //then
        assertThat(contentDto.getText()).isEqualTo("content 내용");
        assertThat(contentDto.getImagesURLs()).containsExactly("https://file.jpg", "https://file2.jpg");
    }

    @Test
    @Transactional
    void 이슈가_가지고있는_이미지가_없어도_빈_ContentDto로_반환() {
        //given
        Project project1 = createProject("프로젝트1", "project1561");
        em.persist(project1);

        Epic epic = Epic.builder()
            .title("이슈 제목")
            .content("content 내용")
            .number(1)
            .status(IssueStatus.DO)
            .assignee(null)
            .project(project1)
            .startDate(LocalDate.of(2024, 2, 19))
            .endDate(LocalDate.of(2024, 2, 23))
            .build();
        em.persist(epic);

        //when
        ContentDto contentDto = epicFactory.createContentDto(epic);

        //then
        assertThat(contentDto.getText()).isEqualTo("content 내용");
        assertThat(contentDto.getImagesURLs()).isEmpty();
    }

    @Test
    @Transactional
    void 이슈의_담당자가_변경되었을때_정상적으로_반영() {
        //given
        Project project1 = createProject("프로젝트1", "project1561");
        em.persist(project1);

        Member member1 = createMember("멤버1");
        em.persist(member1);

        Member member2 = createMember("멤버2");
        em.persist(member2);

        MemberProject memberProject1 = MemberProject.builder()
            .member(member1)
            .project(project1)
            .role(MemberProjectRole.ADMIN)
            .build();

        em.persist(memberProject1);

        MemberProject memberProject2 = MemberProject.builder()
            .member(member2)
            .project(project1)
            .role(MemberProjectRole.ADMIN)
            .build();
        em.persist(memberProject2);

        Epic epic = Epic.builder()
            .title("이슈 제목")
            .content("content 내용")
            .number(1)
            .status(IssueStatus.DO)
            .assignee(member1)
            .project(project1)
            .startDate(LocalDate.of(2024, 2, 19))
            .endDate(LocalDate.of(2024, 2, 23))
            .build();
        em.persist(epic);

        IssueEditRequest request = IssueEditRequest.builder()
            .title("이슈 제목")
            .status(IssueStatus.DO)
            .content("content 내용")
            .files(null)
            .startDate(LocalDate.of(2024, 2, 19))
            .endDate(LocalDate.of(2024, 2, 23))
            .assigneeId(member2.getId())
            .build();

        //when
        Long issueId = epicFactory.updateIssue(epic, project1, request);

        Epic updatedEpic = em.createQuery("select e from Epic e where e.id = :issueId", Epic.class)
            .setParameter("issueId", issueId)
            .getSingleResult();

        //then
        assertThat(updatedEpic.getAssignee().getId()).isEqualTo(member2.getId());
    }


    private Member createMember(String memberName) {
        return Member.builder()
            .name(memberName)
            .build();
    }

    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }
}