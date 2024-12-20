package dynamicquad.agilehub.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
    public void sendEmail(String subject, Map<String, Object> variables, String... to) {
        // Amazon SES를 이용한 이메일 발송
        String content = htmlTemplateEngine.process("invite", createContext(variables));
        SendEmailRequest request = createSendEmailRequest(subject, content, to);

        amazonSimpleEmailService.sendEmail(request);

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
