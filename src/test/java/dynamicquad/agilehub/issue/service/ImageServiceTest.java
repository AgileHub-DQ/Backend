package dynamicquad.agilehub.issue.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import dynamicquad.agilehub.global.util.PhotoS3Manager;
import dynamicquad.agilehub.issue.domain.Issue;
import dynamicquad.agilehub.issue.domain.IssueStatus;
import dynamicquad.agilehub.issue.domain.epic.Epic;
import dynamicquad.agilehub.issue.domain.image.Image;
import dynamicquad.agilehub.project.domain.Project;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
class ImageServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private ImageService imageService;

    @MockBean
    private PhotoS3Manager photoS3Manager;

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

        Issue issue = Epic.builder()
            .title("이슈1")
            .content("이슈1 내용")
            .status(IssueStatus.DO)
            .project(project1)
            .build();
        em.persist(issue);

        when(photoS3Manager.uploadPhotos(files, "/issue")).thenReturn(List.of("https://file.jpg", "https://file2.jpg"));

        //when
        imageService.saveImages(issue, files, "/issue");
        //then
        List<Image> images = em.createQuery("select i from Image i where i.issue = :issue", Image.class)
            .setParameter("issue", em.find(Epic.class, issue.getId()))
            .getResultList();

        assertThat(images).hasSize(2);
        assertThat(images.get(0).getPath()).isNotNull();
        assertThat(images.get(1).getPath()).isEqualTo("https://file2.jpg");


    }

    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }

}