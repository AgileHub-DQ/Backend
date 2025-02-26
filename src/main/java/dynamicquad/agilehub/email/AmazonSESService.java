package dynamicquad.agilehub.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonSESService implements SMTPService {

    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private final TemplateEngine htmlTemplateEngine;

    @Value("${aws.ses.from}")
    private String from;

    @Override
    @Async("emailExecutor")
    @Retryable(retryFor = {GeneralException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public CompletableFuture<Void> sendEmail(String subject, Map<String, Object> variables, String... to) {

        // RetryContext를 통해 현재 시도 횟수 가져오기
        RetryContext context = RetrySynchronizationManager.getContext();
        int attempt = context != null ? context.getRetryCount() + 1 : 1;

        return CompletableFuture.runAsync(() -> {
            try {
                String content = htmlTemplateEngine.process("invite", createContext(variables));
                SendEmailRequest request = createSendEmailRequest(subject, content, to);

                amazonSimpleEmailService.sendEmail(request);
            } catch (Exception e) {
                log.error("이메일 발송 실패 (시도 #{}) - 수신자: {}, 에러: {}",
                    attempt, Arrays.toString(to), e.getMessage());

                log.error("AmazonSESService 이메일 발송 실패: {}", e.getMessage());
                throw new GeneralException(ErrorStatus.EMAIL_NOT_SENT);
            }
        });


    }

    private SendEmailRequest createSendEmailRequest(String subject, String content, String... to) {
        return new SendEmailRequest()
            .withDestination(new Destination().withToAddresses(to))
            .withMessage(new Message()
                .withBody(new Body()
                    .withHtml(new Content().withCharset("UTF-8").withData(content)))
                .withSubject(new Content().withCharset("UTF-8").withData(subject)))
            .withSource(from);
    }

    private IContext createContext(Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        return context;
    }
}
