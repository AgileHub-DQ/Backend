package dynamicquad.agilehub.global.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    private static final boolean WAIT_TASK_COMPLETE = true;
    private static final int AWAIT_TERMINATION_SECONDS = 30;

    @Bean(name = "emailExecutor")
    public Executor emailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // IO 바운드 작업: 코어수 * 2
        executor.setMaxPoolSize(44); // CorePoolSize * (1 + 대기시간/서비스시간)
        executor.setQueueCapacity(80); // (MaxPoolSize - CorePoolSize) * 2
        executor.setThreadNamePrefix("email-");
        executor.setWaitForTasksToCompleteOnShutdown(WAIT_TASK_COMPLETE);
        executor.setAwaitTerminationSeconds(AWAIT_TERMINATION_SECONDS);
        // 거부 정책 설정
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();

        return executor;
    }
}
