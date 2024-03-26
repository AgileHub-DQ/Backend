package dynamicquad.agilehub.issue.service.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.util.PhotoS3Manager;
import dynamicquad.agilehub.issue.controller.request.IssueRequest.IssueCreateRequest;
import dynamicquad.agilehub.issue.controller.request.IssueType;
import dynamicquad.agilehub.issue.domain.Epic;
import dynamicquad.agilehub.issue.domain.Image;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.member.domain.Member;
import dynamicquad.agilehub.project.domain.MemberProject;
import dynamicquad.agilehub.project.domain.MemberProjectRole;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class EpicFactoryTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private EpicFactory epicFactory;

    @MockBean
    private PhotoS3Manager photoS3Manager;

    @Test
    void 이미지없는_에픽이슈를_정상적으로_생성() {
        Project project1 = createProject("프로젝트1", "project1");
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
    void 이미지없는_에픽이슈에_넣은_assinee가_서비스에_존재하지않을때_예외처리() {
        //given
        Project project1 = createProject("프로젝트1", "project1");
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
    void 이미지없는_에픽이슈에_넣은_assignee가_프로젝트에_속하지않을때_예외처리() {
        //given
        Project project1 = createProject("프로젝트1", "project1");
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
    void 이슈에_등록한_이미지두개를_정상적으로_저장() {
        //given
        Project project1 = createProject("프로젝트1", "project1");
        em.persist(project1);

        MultipartFile file1 = new MockMultipartFile("file1", "file1.jpg", MediaType.IMAGE_PNG_VALUE,
            "file1".getBytes());
        MultipartFile file2 = new MockMultipartFile("file2", "file2.jpg", MediaType.IMAGE_PNG_VALUE,
            "file2".getBytes());
        List<MultipartFile> files = List.of(file1, file2);

        IssueCreateRequest request = IssueCreateRequest.builder()
            .title("이슈 제목")
            .type(IssueType.EPIC)
            .status(IssueStatus.DO)
            .content("content 내용")
            .files(files)
            .startDate(LocalDate.of(2024, 2, 19))
            .endDate(LocalDate.of(2024, 2, 23))
            .assigneeId(null)
            .build();

        when(photoS3Manager.uploadPhotos(files, "/issue")).thenReturn(List.of("https://file.jpg", "https://file2.jpg"));

        //when
        Long issueId = epicFactory.createIssue(request, project1);
        //then
        List<Image> images = em.createQuery("select i from Image i where i.issue = :issue", Image.class)
            .setParameter("issue", em.find(Epic.class, issueId))
            .getResultList();

        assertThat(images).hasSize(2);
        assertThat(images.get(0).getPath()).isNotNull();
        assertThat(images.get(1).getPath()).isEqualTo("https://file2.jpg");


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