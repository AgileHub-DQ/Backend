package dynamicquad.agilehub.issue.service.query;

import dynamicquad.agilehub.project.domain.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
class IssueQueryServiceTest {

    @Autowired
    private IssueQueryService issueQueryService;

    @Test
    @Transactional
    void 특정달의_이슈들을_가져오는지_테스트() {

    }

    private Project createProject(String projectName, String projectKey) {
        return Project.builder()
            .name(projectName)
            .key(projectKey)
            .build();
    }

}