package dynamicquad.agilehub.issue.domain.image;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class ImageRepositoryTest {

    @Autowired
    private ImageRepository imageRepository;

    @Test
    @Transactional
    void 이미지경로들_지우기() {
        // given
        String path = "path";
        Image image = Image.builder().path(path).build();
        imageRepository.save(image);

        // when
        imageRepository.deletePaths(List.of(path));

        // then
        assertEquals(0, imageRepository.findAll().size());
    }
}