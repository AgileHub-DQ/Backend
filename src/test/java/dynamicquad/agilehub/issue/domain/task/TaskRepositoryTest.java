package dynamicquad.agilehub.issue.domain.task;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.orm.jpa.JpaSystemException;

@SpringBootTest
@Profile("local")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void 로컬에서_저장한_이슈들_정상적으로_가져오는지_조회() {
        // given
        List<Long> storyIds = new ArrayList<>();
        for (long i = 101L; i < 20100L; i++) {
            storyIds.add(i);
        }
        // when
        // then

        Assertions.assertThatThrownBy(() -> taskRepository.findTasksByStoryIds(storyIds))
            .isInstanceOf(JpaSystemException.class);
    }
}