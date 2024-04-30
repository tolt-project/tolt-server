
package tolt.server.security.util;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.lang.StringBuilder;

public class Hashing {

    public static byte[] getHash64 (byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-512").digest(input);
        } catch (Exception e) {} return new byte[0];
    }
    public static byte[] getHash48 (byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-384").digest(input);
        } catch (Exception e) {} return new byte[0];
    }
    public static byte[] getHash32 (byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (Exception e) {} return new byte[0];
    }
    public static byte[] getHash20 (byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-1").digest(input);
        } catch (Exception e) {} return new byte[0];
    }
    public static byte[] getHash16 (byte[] input) {
        try {
            return MessageDigest.getInstance("MD5").digest(input);
        } catch (Exception e) {} return new byte[0];
    }

    public static String getHexOf (byte[] input) {

        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : input) {

            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) stringBuilder.append('0');
            stringBuilder.append(hex);
        }

        return stringBuilder.toString().toUpperCase();
    }
}
