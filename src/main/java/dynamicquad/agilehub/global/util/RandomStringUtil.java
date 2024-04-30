package dynamicquad.agilehub.global.util;

import java.security.SecureRandom;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomStringUtil {
    private static final String CHAR_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_DIGITS = "0123456789";
    private static final String CHAR_SPECIAL = "!@#$%^&*";

    private static final String ALL_CHARS = CHAR_UPPERCASE + CHAR_LOWERCASE + CHAR_DIGITS + CHAR_SPECIAL;

    public String generateRandomKey(int keyLength) {
        StringBuilder sb = new StringBuilder(keyLength);
        Random random = new SecureRandom();
        for (int i = 0; i < keyLength; i++) {
            int randomIndex = random.nextInt(ALL_CHARS.length());
            sb.append(ALL_CHARS.charAt(randomIndex));
        }

        log.info("Generated Random Key : {}", sb);

        return sb.toString();
    }

}
