package org.lisovskyi_ivanov.backend.utility;

import java.security.SecureRandom;

public final class StringGenerator {
    private StringGenerator() {
        throw new UnsupportedOperationException("StringGenerator is a utility class and cannot be instantiated");
    }

    private static final String BASE62 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final SecureRandom random = new SecureRandom();

    public static String generateUniqueString(int maxLength) {
        long timestamp = System.currentTimeMillis();
        int salt = random.nextInt(1000);

        long uniqueValue = timestamp * 1000 + salt;

        StringBuilder sb = new StringBuilder();
        while (uniqueValue > 0) {
            sb.append(BASE62.charAt((int) (uniqueValue % 62)));
            uniqueValue /= 62;
        }

        String result = sb.reverse().toString();
        if (result.length() > maxLength) {
            return result.substring(result.length() - maxLength);
        }
        return result;
    }
}
