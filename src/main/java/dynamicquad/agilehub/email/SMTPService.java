package dynamicquad.agilehub.email;

import java.util.Map;

public interface SMTPService {
    void sendEmail(String subject, Map<String, Object> variables, String... to);
}
