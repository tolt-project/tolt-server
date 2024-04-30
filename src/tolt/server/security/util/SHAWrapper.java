
package tolt.server.security.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.lang.StringBuilder;

public class SHAWrapper {

    public static byte[] sha256Raw (byte[] input) { try {

        MessageDigest algorithm = MessageDigest.getInstance("SHA-256");
        return algorithm.digest(input);

        } catch (NoSuchAlgorithmException e) { e.printStackTrace(); }
        return new byte[0];
    }
    public static byte[] sha256Raw (String input) {

        return sha256Raw(input.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha256Text (byte[] input) {

        byte[] hashedData = sha256Raw(input);

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : hashedData) {

            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) stringBuilder.append('0');
            stringBuilder.append(hex);
        }

        return stringBuilder.toString().toUpperCase();
    }

    public static String sha256Text (String input) {

        return sha256Text(input.getBytes(StandardCharsets.UTF_8));
    }
}
