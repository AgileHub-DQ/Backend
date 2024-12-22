package dynamicquad.agilehub.global.util;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RandomStringUtil {
    private static final String CHAR_UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String CHAR_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_DIGITS = "0123456789";
    private static final String CHAR_SPECIAL = "!@#$%^&*";

    private static final String ALL_CHARS = CHAR_UPPERCASE + CHAR_LOWERCASE + CHAR_DIGITS + CHAR_SPECIAL;


    private RandomStringUtil() {
    }

    public static String generateRandomKey(int keyLength) {
        StringBuilder sb = new StringBuilder(keyLength);
        Random random = new SecureRandom();
        for (int i = 0; i < keyLength; i++) {
            int randomIndex = random.nextInt(ALL_CHARS.length());
            sb.append(ALL_CHARS.charAt(randomIndex));
        }

        log.info("Generated Random Key : {}", sb);

        return sb.toString();
    }

    public static String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }

    public static String uuidToBase64(String str) {
        UUID uuid = UUID.fromString(str);
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bb.array());
    }

    public static String base64ToUUID(String str) {
        byte[] bytes = Base64.getDecoder().decode(str);
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        UUID uuid = new UUID(bb.getLong(), bb.getLong());

        return uuid.toString();
    }


}
