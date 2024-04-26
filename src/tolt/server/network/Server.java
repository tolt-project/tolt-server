
package tolt.server.network;

import java.lang.Thread;
import java.security.SecureRandom;
import java.security.PrivateKey;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.net.InetAddress;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;

import tolt.server.security.Loading.PemLoader;
import tolt.server.service.Logging;

public class Server {

    private static boolean running = false, shouldStop = false;
    public static boolean isRunning ()
        { return running; }

    public static void start () {

        PrivateKey privateKey = PemLoader.loadPrivateKey("private-key.pem"); //hard coded for now
        X509Certificate certificate = PemLoader.loadX509Certificate("server-cert.pem");

        if (privateKey == null) {
            Logging.log("Failed to start server, privateKey == null!");
            return;
        }
        if (certificate == null) {
            Logging.log("Failed to start server, certificate == null!");
            return;
        }

        try {

            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, null);
            keyStore.setKeyEntry("default", privateKey, new char[0], new X509Certificate[]{certificate});

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, new char[0]);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());


            SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
            sslServerSocket = (SSLServerSocket)sslServerSocketFactory.createServerSocket(
                8282, 50, InetAddress.getByName("0.0.0.0") //hardcoded for now
            );

            mainThread = new Thread () {
                public void run () { acceptLoop(); }
            };
            mainThread.start();

            running = true; shouldStop = false;

            Logging.log("Server has started!");

        } catch (Exception e) {

            Logging.log("Failed to start server!");
            e.printStackTrace();
        }
    }
    public static void stop () {

        shouldStop = true;
        try { sslServerSocket.close(); } catch (Exception e) {}
        running = false;
    }

    private static Thread mainThread = null;
    private static SSLServerSocket sslServerSocket = null;

    private static void acceptLoop () {

        while (!shouldStop) {

            try {

                SSLSocket socket = (SSLSocket)sslServerSocket.accept();

                ///////////////////// TEMP TEST
                    byte[] recv = new byte[5];
                    socket.getInputStream().read(recv, 0, 5);
                    System.out.println(new String(recv, "ASCII"));

                    socket.getOutputStream().write("olleh".getBytes("ASCII"), 0, 5);

                    socket.close();
                ///////////////////// TEMP TEST

            } catch (Exception e) {

                if (!shouldStop) {

                    System.out.println("accpetLoop Exception!");
                    e.printStackTrace();
                }
            }
        }

        Logging.log("Server has stopped...");
    }
}
