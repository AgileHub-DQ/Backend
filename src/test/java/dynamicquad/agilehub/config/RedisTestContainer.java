package dynamicquad.agilehub.config;

import java.time.Duration;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

public class RedisTestContainer implements BeforeAllCallback, AfterAllCallback {
    private static final String REDIS_DOCKER_IMAGE = "redis:7.0";
    private static final GenericContainer<?> REDIS_CONTAINER;

    static {
        REDIS_CONTAINER = new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
            .withExposedPorts(6379)
            .withReuse(true)
            .withStartupTimeout(Duration.ofSeconds(60));
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        try {
            REDIS_CONTAINER.start();
        } catch (Exception e) {
            throw new RuntimeException("Redis Container 시작 실패", e);
        }
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        if (REDIS_CONTAINER.isRunning()) {
            REDIS_CONTAINER.stop();
        }
    }

    @DynamicPropertySource
    static void setRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }
}
