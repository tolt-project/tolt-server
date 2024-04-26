
package tolt.server.security;

import java.io.File;
import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.security.PrivateKey;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import tolt.server.service.logging.Logging;

public class Loading {

    public static class PemLoader {

        public static PrivateKey loadPrivateKey (String pemFilePath) {

            try {

                File pemFile = new File(pemFilePath);
                if (!pemFile.exists() || pemFile.isDirectory()) return null;
                String pemData = Files.readString(pemFile.toPath());

                String decodeCache = "";
                for (final String row : pemData.split(System.lineSeparator()))
                    if (!row.contains("KEY-----")) decodeCache += row;
                byte[] derData = Base64.getDecoder().decode(decodeCache);

                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(derData));

                return privateKey;

            } catch (Exception e) {

                Logging.stackErr(e);
                return null;
            }
        }

        public static X509Certificate loadX509Certificate (String pemFilePath) {

            try {

                File pemFile = new File(pemFilePath);
                if (!pemFile.exists() || pemFile.isDirectory()) return null;
                String pemData = Files.readString(pemFile.toPath());

                String decodeCache = "";
                for (final String row : pemData.split(System.lineSeparator()))
                    if (!row.contains("CERTIFICATE-----")) decodeCache += row;
                byte[] derData = Base64.getDecoder().decode(decodeCache);

                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                X509Certificate certificate
                    = (X509Certificate)certificateFactory.generateCertificate(new ByteArrayInputStream(derData));

                return certificate;

            } catch (Exception e) {

                Logging.stackErr(e);
                return null;
            }
        }
    }
}
