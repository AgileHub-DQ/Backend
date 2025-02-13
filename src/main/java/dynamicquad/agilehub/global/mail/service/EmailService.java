package dynamicquad.agilehub.global.mail.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.mail.model.EmailInfo;
import dynamicquad.agilehub.project.model.InviteEmailInfo;
import dynamicquad.agilehub.report.SummaryEmailInfo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async("emailExecutor")
    @Retryable(retryFor = {GeneralException.class}, maxAttempts = 3, backoff = @Backoff(delay = 500))
    public CompletableFuture<Void> sendMail(EmailInfo emailInfo, String type) {
        return CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = createMimeMessage(emailInfo, type);
                mailSender.send(message);
            } catch (Exception e) {
                log.error("Gmail SMTP 이메일 발송 실패: {}", e.getMessage());
                throw new GeneralException(ErrorStatus.EMAIL_NOT_SENT);
            }
        });
    }

    private MimeMessage createMimeMessage(EmailInfo emailInfo, String type) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

        helper.setTo(emailInfo.getTo());
        helper.setSubject(emailInfo.getSubject());
        helper.setText(getEmailContent(emailInfo, type), true);

        return mimeMessage;
    }

    private String getEmailContent(EmailInfo emailInfo, String type) {
        return StringUtils.hasText(type)
            ? setContext(emailInfo, type)
            : emailInfo.getMessage();
    }

    private String setContext(EmailInfo emailInfo, String type) {
        Context context = new Context();
        if (type.equals("invite") && emailInfo instanceof InviteEmailInfo inviteEmailMessage) {
            context.setVariable("projectName", inviteEmailMessage.getProjectName());
            context.setVariable("inviteCode", inviteEmailMessage.getInviteCode());
        }
        else if (type.equals("reportSummary") && emailInfo instanceof SummaryEmailInfo summaryEmailMessage) {
            context.setVariable("projectName", summaryEmailMessage.getProjectName());
            context.setVariable("report", summaryEmailMessage.getReport());
        }
        return templateEngine.process(type, context);
    }

}
