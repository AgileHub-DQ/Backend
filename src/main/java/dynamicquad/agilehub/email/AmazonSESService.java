package dynamicquad.agilehub.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.issue.aspect.Retry;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

@Service
@RequiredArgsConstructor
public class AmazonSESService implements SMTPService {

    private final AmazonSimpleEmailService amazonSimpleEmailService;
    private final TemplateEngine htmlTemplateEngine;

    @Value("${aws.ses.from}")
    private String from;

    @Override
    @Async("emailExecutor")
    @Retry(maxRetries = 3, retryFor = {GeneralException.class}, delay = 1000)
    public CompletableFuture<Void> sendEmail(String subject, Map<String, Object> variables, String... to) {
        // Amazon SES를 이용한 이메일 발송
        return CompletableFuture.runAsync(() -> {
            try {
                String content = htmlTemplateEngine.process("invite", createContext(variables));
                SendEmailRequest request = createSendEmailRequest(subject, content, to);

                amazonSimpleEmailService.sendEmail(request);
            } catch (Exception e) {
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
