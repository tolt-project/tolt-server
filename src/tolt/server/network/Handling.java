
package tolt.server.network;

import java.lang.Thread;
import java.util.Vector;
import java.util.Queue;
import java.util.LinkedList;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLSocket;
import java.net.Socket;

import tolt.server.service.logging.Logging;

public class Handling {

    private static Object mutex = new Object();

    private static Queue<SSLSocket> newClients = new LinkedList<SSLSocket>();
    public static void queueClient (SSLSocket socket) {
        synchronized (mutex) { newClients.add(socket); }
    }
    public static int newClientCount () {
        synchronized (mutex) { return newClients.size(); }
    }
    public static SSLSocket popClientQueue () {
        synchronized (mutex) { return newClients.remove(); }
    }


    public static void start () {

        running = true; shouldStop = false;
        new Thread () {
            public void run () { loop(); }
        }.start();
    }
    public static void stop () {

        shouldStop = true;
        while (running) try { Thread.sleep(10);
        } catch (Exception e) {}
    }

    private static boolean running = false, shouldStop = false;

    private static void loop () {

        Logging.log("Starting Client-handler..");

        while (!shouldStop) { try {

            if (newClientCount() != 0) {

                SSLSocket socket = popClientQueue();
                clients.add(socket);
                streams.add(socket.getOutputStream());
                recvLoop(socket.getInputStream());
            }

            sendLoop();

        } catch (Exception e) { Logging.stackWarn(e); } }

        Logging.log("Closing all client sockets..");
        clients.forEach(client -> { try {

            client.close();

        } catch (Exception e) {} });

        Logging.log("Client-handler has stopped.");
        running = false;
    }


    private static Vector<SSLSocket> clients = new Vector<SSLSocket>();
    private static Vector<OutputStream> streams = new Vector<OutputStream>();

    private static void sendLoop () {

        //if there is data to send to the clients, which at this point
        //there is not, we send it to all clients, or to respective client
        //if there is nothing then we wait
        try { Thread.sleep(10);
        } catch (Exception e) {}
    }


    private static void recvLoop (InputStream stream) {

        new Thread () { public void run () {

            byte[] cacheBuffer = new byte[1];
            ByteBuffer recvBuffer = ByteBuffer.allocate(2);
            short packetId = 0; int packetSize = 0;
            int recvBytes = 0, state = 0;

            while (true) { try {

                recvBytes = stream.read(cacheBuffer, 0, 1);
                if (recvBytes <= 0) break;

                recvBuffer.put(cacheBuffer[0]);

                switch (state) {

                    case 0: {
                        if (recvBuffer.position() != 2) break;

                        recvBuffer.rewind();
                        packetId = recvBuffer.getShort();
                        recvBuffer = ByteBuffer.allocate(4);
                        state = 1;

                    break; }

                    case 1: {
                        if (recvBuffer.position() != 4) break;

                        //later add a check here to stop HUGE packets being send

                        recvBuffer.rewind();
                        packetSize = recvBuffer.getInt();
                        recvBuffer = ByteBuffer.allocate(packetSize);
                        state = 2;

                    break; }

                    case 2: {
                        if (recvBuffer.position() != packetSize) break;

                        String cache = "";
                        for (byte b : recvBuffer.array()) cache += b +", ";
                        Logging.log(
                            "packetId: " + packetId +
                            ", packetSize: " + packetSize +
                            ", packetData: [" + cache + "]"
                        );

                        recvBuffer = ByteBuffer.allocate(2);
                        state = 0;

                    break; }
                }

            } catch (Exception e) {

                Logging.stackWarn(e);
            } }

        } }.start();
    }
}
