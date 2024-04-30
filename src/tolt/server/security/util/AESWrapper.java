
package tolt.server.security.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class AESWrapper {

    public static byte[] encrypt (byte[] input, byte[] key, byte[] iv) {

        try {

            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

            return aes.doFinal(input);

        } catch (Exception e) { e.printStackTrace(); }
        return new byte[0];
    }

    public static byte[] decrypt (byte[] input, byte[] key, byte[] iv) {

        try {

            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, "AES"), new IvParameterSpec(iv));

            return aes.doFinal(input);

        } catch (Exception e) { e.printStackTrace(); }
        return new byte[0];
    }
}
