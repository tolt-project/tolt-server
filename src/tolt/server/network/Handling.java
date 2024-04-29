
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
import tolt.server.network.cache.Cache;
import tolt.server.network.module.Packet;

public class Handling {

    public static void queueIncoming (SSLSocket socket) {
        Cache.Incoming.enqueue(socket);
    }
    public static void queueDisconnect (int id) {
        Cache.Disconnecting.enqueue(id);
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

        Logging.log("Started Client-handler.");

        while (!shouldStop) { try {

            if (Cache.Incoming.count() != 0) {

                SSLSocket socket = Cache.Incoming.dequeue();
                int id = Cache.initEntry(socket);
                recvLoop(id, socket.getInputStream());

                Logging.log(
                    socket.getRemoteSocketAddress().toString() + " is " + id
                );
            }

            if (Cache.Disconnecting.count() != 0) {

                int id = Cache.Disconnecting.dequeue();
                Cache.killEntry(id);

                Logging.log(id + " has disconnected...");
            }

            sendLoop();

        } catch (Exception e) { Logging.stackWarn(e); } }

        Logging.log("Closing all client sockets..");
        for (var socket : Cache.getAllSockets()) { try {

            socket.close();

        } catch (Exception e) {} }

        Logging.log("Client-handler has stopped.");
        running = false;
    }


    private static boolean actioned = false;
    private static int idCache = -1;
    private static void sendLoop () {

        actioned = false;

        for (int i = 0; i < Cache.size(); ++i) {

            idCache = Cache.getIdByIndex(i);

            if (Cache.IOQueues.Send.isEmpty(idCache)) continue;

            try {

                Cache.getStreamByIndex(i).write(Cache.IOQueues.Send.pop(idCache));
                actioned = true;

            } catch (Exception e) {

                Logging.warn(idCache + ": " + e.getMessage());
            }
        }

        if (!actioned) try { Thread.sleep(10);
        } catch (Exception e) {}
    }

    private static void recvLoop (int id, InputStream stream) {

        new Thread () { public void run () {

            byte[] cacheBuffer = new byte[1];
            ByteBuffer recvBuffer = ByteBuffer.allocate(2);
            short packetId = 0; int packetSize = 0;
            int recvBytes = 0, state = 0;

            if (!Authentication.authenticate(id, stream)) {

            } else try { while (true) {

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

                        ///////////////////////////// TEMP
                            String cache = "";
                            for (byte b : recvBuffer.array()) cache += b +", ";
                            Logging.debug(id +
                                ": packetId: " + packetId +
                                ", packetSize: " + packetSize +
                                ", packetData: [" + cache + "]"
                            );
                        ///////////////////////////// TEMP

                        Cache.IOQueues.Recv.queue(new Packet(
                            id, packetId, recvBuffer.array()
                        ));

                        recvBuffer = ByteBuffer.allocate(2);
                        state = 0;

                    break; }
                }

            } } catch (Exception e) {

                Logging.warn(id + ": " + e.getMessage());
            }

            queueDisconnect(id);

        } }.start();
    }
}
