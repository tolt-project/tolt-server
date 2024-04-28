
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
import tolt.server.service.logging.Logging;
import tolt.server.service.Config;
import tolt.server.service.stats.Stats;

public class Server {

    private static boolean running = false, shouldStop = false;
    public static boolean isRunning ()
        { return running; }

    public static void start () {

        PrivateKey privateKey = PemLoader.loadPrivateKey(Config.getString("server.private-key-path"));
        X509Certificate certificate = PemLoader.loadX509Certificate(Config.getString("server.server-cert-path"));

        if (privateKey == null) {
            Logging.warn("Failed to start server, privateKey == null!");
            return;
        }
        if (certificate == null) {
            Logging.warn("Failed to start server, certificate == null!");
            return;
        }

        Logging.log(String.format(
            "Starting Server on `%s:%s'...",
            Config.getString("server.ipaddress"), Config.getInt("server.port")
        ));

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
                Config.getInt("server.port"), 50, InetAddress.getByName(Config.getString("server.ipaddress")) //hardcoded for now
            );

            mainThread = new Thread () {
                public void run () { catchLoop(); }
            };
            mainThread.start();

            running = true; shouldStop = false;

            Logging.log("Server has started!");

        } catch (Exception e) {

            Logging.crit("Failed to start server!");
            Logging.stackErr(e);
        }
    }
    public static void stop () {

        shouldStop = true;
        try { sslServerSocket.close(); } catch (Exception e) {}
        running = false;
    }

    private static Thread mainThread = null;
    private static SSLServerSocket sslServerSocket = null;

    private static void catchLoop () {

        while (!shouldStop) {

            try {

                SSLSocket socket = (SSLSocket)sslServerSocket.accept();

                Logging.log("Caught client:" + socket.getRemoteSocketAddress().toString());
                Stats.increment("catching.client-catches");

                Handling.queueIncoming(socket);

            } catch (Exception e) {

                if (!shouldStop) {

                    Stats.increment("catching.client-misses");
                    Logging.warn("Failed to catch Client: " + e.getMessage());
                }
            }
        }

        Logging.log("Server has stopped...");
    }
}
