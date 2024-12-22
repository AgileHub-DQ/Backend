package dynamicquad.agilehub.email;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface SMTPService {
    CompletableFuture<Void> sendEmail(String subject, Map<String, Object> variables, String... to);
}
