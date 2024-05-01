package dynamicquad.agilehub.global.mail.service;

import dynamicquad.agilehub.global.exception.GeneralException;
import dynamicquad.agilehub.global.header.status.ErrorStatus;
import dynamicquad.agilehub.global.mail.model.EmailInfo;
import dynamicquad.agilehub.project.model.InviteEmailInfo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendMail(EmailInfo emailInfo, String type) {
        MimeMessage mimeMailMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMailMessage, false, "UTF-8");
            helper.setTo(emailInfo.getTo());
            helper.setSubject(emailInfo.getSubject());
            if (StringUtils.hasText(type)) {
                helper.setText(setContext(emailInfo, type), true);
            } else {
                helper.setText(emailInfo.getMessage());
            }
            mailSender.send(mimeMailMessage);
        } catch (MessagingException me) {
            throw new GeneralException(ErrorStatus.EMAIL_NOT_SENT);
        }
    }

    private String setContext(EmailInfo emailInfo, String type) {
        Context context = new Context();
        if (type.equals("invite") && emailInfo instanceof InviteEmailInfo inviteEmailMessage) {
            context.setVariable("projectName", inviteEmailMessage.getProjectName());
            context.setVariable("inviteCode", inviteEmailMessage.getInviteCode());
        }
        return templateEngine.process(type, context);
    }

}
