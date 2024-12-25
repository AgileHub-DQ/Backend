package dynamicquad.agilehub.config;

import java.time.Duration;
import org.junit.jupiter.api.DisplayName;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@DisplayName("Redis Test Container")
@ActiveProfiles("test")
@Configuration
public class RedisTestContainer {
    private static final String REDIS_DOCKER_IMAGE = "redis:7.0";

    static {
        try {
            GenericContainer<?> REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
                .withExposedPorts(6379)
                .withReuse(true)
                .withStartupTimeout(Duration.ofSeconds(60));  // 60초로 설정

            REDIS_CONTAINER.start();

            System.setProperty("spring.redis.host", REDIS_CONTAINER.getHost());
            System.setProperty("spring.redis.port", REDIS_CONTAINER.getMappedPort(6379).toString());
        } catch (Exception e) {
            throw new RuntimeException("Redis Container 시작 실패", e);
        }
    }


}