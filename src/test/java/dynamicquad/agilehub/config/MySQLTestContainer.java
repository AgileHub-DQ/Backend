package dynamicquad.agilehub.config;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

public class MySQLTestContainer implements BeforeAllCallback, AfterAllCallback {

    private static final String MYSQL_DOCKER_IMAGE = "mysql:8.0";
    private static final MySQLContainer<?> MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>(DockerImageName.parse(MYSQL_DOCKER_IMAGE))
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true); // 컨테이너 재사용 가능하도록 설정
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        try {
            MYSQL_CONTAINER.start();
        } catch (Exception e) {
            throw new RuntimeException("MySQL Container 시작 실패", e);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (MYSQL_CONTAINER.isRunning()) {
            MYSQL_CONTAINER.stop();
        }
    }

    @DynamicPropertySource
    static void setMySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL_CONTAINER::getDriverClassName);
    }
}