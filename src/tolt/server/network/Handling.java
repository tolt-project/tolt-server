
package tolt.server.network;

import java.lang.Math;
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
import tolt.server.core.cache.SessionCache;

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
                SessionCache.unsetById(id);

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
    private static int idCache = -1, capacity;
    private static byte[] sendCache;
    private static final int packetChunkMax = 10;
    private static void sendLoop () {

        actioned = false;

        for (int i = 0; i < Cache.size(); ++i) {

            idCache = Cache.getIdByIndex(i);

            if (Cache.IOQueues.Send.isEmpty(idCache)) continue;

            try {

                sendCache = Cache.IOQueues.Send.pop(idCache);
                capacity = packetChunkMax;

                    Logging.debug("");
                    Logging.debug("In length: " + sendCache.length);
                    Logging.debug("Capacity: " + capacity);

                if (capacity > sendCache.length) {

                    Cache.getStreamByIndex(i).write(sendCache, 0, sendCache.length);

                        String c = ""; for (int x = 0; x < sendCache.length; ++x) c += sendCache[x] + ",";
                        Logging.debug(String.format(
                            "%d of %d (%d) was sent [%sEOB]", i, Cache.size(), idCache, c));

                } else {

                    if (sendCache.length % capacity != 0) {
                        while (sendCache.length % capacity < 6 && capacity > 6)
                            capacity--;


                        Logging.debug("Adjusted capacity: " + capacity);
                    }

                    int count = -java.lang.Math.floorDiv(-sendCache.length, capacity);
                    for (int s = 0; s < count; ++s) {

                        int size = java.lang.Math.min(capacity, sendCache.length - (s * capacity));
                        byte[] section = new byte[size];
                        System.arraycopy(sendCache, s * capacity, section, 0, size);

                        Cache.getStreamByIndex(i).write(section, 0, size);

                            String c = ""; for (int x = 0; x < size; ++x) c += section[x] + ",";
                            Logging.debug(String.format(
                                "%d of %d (%d) was sent section %d: [%sEOB]", i, Cache.size(), idCache, s, c));
                    }
                }
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

                        if (packetSize == 0) {

                            ///////////////////////////// TEMP
                                Logging.debug(id +
                                    ": packetId: " + packetId +
                                    ", packetSize: " + packetSize +
                                    ", packetData: [null]"
                                );
                            ///////////////////////////// TEMP

                            Cache.IOQueues.Recv.queue(new Packet(
                                id, packetId, new byte[0]));

                            recvBuffer = ByteBuffer.allocate(2);
                            state = 0;

                        } else {
                            recvBuffer = ByteBuffer.allocate(packetSize);
                            state = 2;
                        }

                    break; }

                    case 2: {
                        if (recvBuffer.position() != packetSize) break;

                        ///////////////////////////// TEMP
                            String cache = "";
                            for (byte b : recvBuffer.array()) cache += b +", ";
                            Logging.debug(id +
                                ": packetId: " + packetId +
                                ", packetSize: " + packetSize +
                                ", packetData: [" + cache + "EOB]"
                            );
                        ///////////////////////////// TEMP

                        Cache.IOQueues.Recv.queue(new Packet(
                            id, packetId, recvBuffer.array()));

                        recvBuffer = ByteBuffer.allocate(2);
                        state = 0;

                    break; }
                }

            } } catch (Exception e) {

                // Logging.warn(id + ": " + e.getMessage());
                Logging.warn(id + ": ");
                Logging.stackWarn(e);
            }

            queueDisconnect(id);

        } }.start();
    }
}
