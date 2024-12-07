package dynamicquad.agilehub.global.mail.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.mail.model.EmailInfo;
import dynamicquad.agilehub.issue.aspect.Retry;
import dynamicquad.agilehub.project.model.InviteEmailInfo;
import dynamicquad.agilehub.report.SummaryEmailInfo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;

import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async("emailExecutor")
    @Retry(maxRetries = 3, // 최대 3번 시도
            retryFor = { MessagingException.class, MailSendException.class }, // 이메일 관련 예외만 재시도
            delay = 1000 // 1초 대기 후 재시도
    )
    public CompletableFuture<Void> sendMail(EmailInfo emailInfo, String type) {
        return CompletableFuture.runAsync(() -> {
            try {
                MimeMessage message = createMimeMessage(emailInfo, type);
                mailSender.send(message);
            } catch (MessagingException me) {
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
        } else if (type.equals("reportSummary") && emailInfo instanceof SummaryEmailInfo summaryEmailMessage) {
            context.setVariable("projectName", summaryEmailMessage.getProjectName());
            context.setVariable("report", summaryEmailMessage.getReport());
        }
        return templateEngine.process(type, context);
    }

}
