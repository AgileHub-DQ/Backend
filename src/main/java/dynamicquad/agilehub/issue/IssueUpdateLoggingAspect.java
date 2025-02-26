package dynamicquad.agilehub.issue;

import dynamicquad.agilehub.issue.dto.IssueRequestDto;
import dynamicquad.agilehub.member.dto.MemberRequestDto.AuthMember;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class IssueUpdateLoggingAspect {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    @Around("execution(* dynamicquad.agilehub.issue.service.command.IssueService.updateIssue(..)) && args(key, issueId, request, authMember)")
    public Object logIssueUpdate(ProceedingJoinPoint joinPoint,
                                 String key, Long issueId, IssueRequestDto.EditIssue request, AuthMember authMember)
        throws Throwable {

        String threadName = Thread.currentThread().getName();
        String requestContent = request.getContent();
        String currentTime = LocalDateTime.now().format(formatter);

        log.warn("[동시성 추적 - {}] Thread: {}, 이슈: {}, 사용자: {}, 수정 요청 내용: '{}'",
            currentTime,
            threadName,
            issueId,
            authMember.getId(),
            requestContent);
        Object result;

        result = joinPoint.proceed();

        return result;
    }
}
