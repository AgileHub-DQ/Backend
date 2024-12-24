package dynamicquad.agilehub.issue.aspect;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@Slf4j
public class RetryAspect {

    @Around("@annotation(dynamicquad.agilehub.issue.aspect.Retry)")
    public Object retry(ProceedingJoinPoint joinPoint) throws Throwable {
        // Retry 어노테이션 정보 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Retry retry = signature.getMethod().getAnnotation(Retry.class);

        int attempts = 0;
        int maxAttempts = retry.maxRetries();

        while (attempts < maxAttempts) {
            try {
                log.error("시도 #{}", attempts + 1);
                return joinPoint.proceed();
            } catch (Exception e) {
                attempts++;

                // 설정된 예외 타입들 중 하나와 일치하는지 확인
                boolean shouldRetry = Arrays.stream(retry.retryFor())
                    .anyMatch(exceptionType -> exceptionType.isInstance(e));

                if (!shouldRetry) {
                    throw e; // 재시도 대상이 아닌 예외는 즉시 던짐
                }

                if (attempts == maxAttempts) {
                    log.error("{}회 시도 후 실패", maxAttempts, e);
                    throw e;
                }

                log.error("재시도 {}/{}", attempts, maxAttempts);
                Thread.sleep(retry.delay());
            }
        }
        throw new RuntimeException("예기치 않은 재시도 실패");
    }
}
