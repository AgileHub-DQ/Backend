package dynamicquad.agilehub.global.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

class RandomStringUtilTest {
    // SecureRandom 32자와 UUID.randomUUID() 성능 비교

    private static final int ITERATIONS = 1_000_000;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static void main(String[] args) {
        // Test different lengths for SecureRandom
        int[] lengths = {16, 32, 36, 64};  // 36은 UUID 길이와 동일

        for (int length : lengths) {
            System.out.println("\nTesting with length: " + length);

            // Test generation time
            long startTime = System.currentTimeMillis();
            List<String> secureRandomTokens = generateSecureRandomTokens(length);
            long secureRandomTime = System.currentTimeMillis() - startTime;

            startTime = System.currentTimeMillis();
            List<String> uuidTokens = generateUuidTokens();
            long uuidTime = System.currentTimeMillis() - startTime;

            // Test collision rate
            int secureRandomCollisions = checkCollisions(secureRandomTokens);
            int uuidCollisions = checkCollisions(uuidTokens);

            // Calculate memory usage (approximate)
            long secureRandomMemory = calculateMemoryUsage(secureRandomTokens);
            long uuidMemory = calculateMemoryUsage(uuidTokens);

            // Print results
            System.out.println("SecureRandom Performance:");
            System.out.println("  Generation time: " + secureRandomTime + "ms");
            System.out.println("  Collisions: " + secureRandomCollisions);
            System.out.println("  Memory usage: " + secureRandomMemory + " bytes");

            System.out.println("UUID Performance:");
            System.out.println("  Generation time: " + uuidTime + "ms");
            System.out.println("  Collisions: " + uuidCollisions);
            System.out.println("  Memory usage: " + uuidMemory + " bytes");
        }
    }

    private static List<String> generateSecureRandomTokens(int length) {
        List<String> tokens = new ArrayList<>(ITERATIONS);
        for (int i = 0; i < ITERATIONS; i++) {
            StringBuilder token = new StringBuilder(length);
            for (int j = 0; j < length; j++) {
                token.append(CHARS.charAt(secureRandom.nextInt(CHARS.length())));
            }
            tokens.add(token.toString());
        }
        return tokens;
    }

    private static List<String> generateUuidTokens() {
        List<String> tokens = new ArrayList<>(ITERATIONS);
        for (int i = 0; i < ITERATIONS; i++) {
            tokens.add(UUID.randomUUID().toString());
        }
        return tokens;
    }

    private static int checkCollisions(List<String> tokens) {
        Set<String> uniqueTokens = new HashSet<>(tokens);
        return tokens.size() - uniqueTokens.size();
    }

    private static long calculateMemoryUsage(List<String> tokens) {
        // Rough estimation of memory usage
        return tokens.get(0).length() * Character.BYTES * (long) tokens.size();
    }


}