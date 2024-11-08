package dynamicquad.agilehub.issue.aspect;

import static dynamicquad.agilehub.global.header.status.ErrorStatus.OPTIMISTIC_LOCK_EXCEPTION_ISSUE_NUMBER;

import dynamicquad.agilehub.global.exception.GeneralException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Aspect
@Order(Ordered.LOWEST_PRECEDENCE - 1)  // 트랜잭션보다 더 낮은 순서로 실행
@Component
@Slf4j
public class RetryAspect {
    private static final int MAX_RETRIES = 5;

    @Around("@annotation(dynamicquad.agilehub.issue.aspect.Retry)")
    public Object retry(ProceedingJoinPoint joinPoint) throws Throwable {
        int attempts = 0;
        while (attempts < MAX_RETRIES) {
            try {
                log.info("시작 시도: {}", attempts + 1);
                return joinPoint.proceed();
            } catch (ObjectOptimisticLockingFailureException e) {
                attempts++;
                if (attempts == MAX_RETRIES) {
                    log.error("Failed after {} attempts", MAX_RETRIES, e);
                    throw new GeneralException(OPTIMISTIC_LOCK_EXCEPTION_ISSUE_NUMBER);
                }
                log.warn("Retry attempt {} of {}", attempts, MAX_RETRIES);
                Thread.sleep(100); // 재시도 전 잠시 대기
            }
        }
        throw new GeneralException(OPTIMISTIC_LOCK_EXCEPTION_ISSUE_NUMBER);
    }
}
