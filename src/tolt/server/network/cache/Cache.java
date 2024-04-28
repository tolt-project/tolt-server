
package tolt.server.network.cache;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Vector;
import java.util.HashMap;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;

import tolt.server.network.module.Packet;

public class Cache {

    private static Vector<CacheEntry> cache = new Vector<CacheEntry>();

    public static int initEntry (SSLSocket socket) {

        IOQueues.Send.init(socket.hashCode());
        cache.add(new CacheEntry(socket));
        return socket.hashCode();
    }

    public static void killEntry (int id) {

        int index = -1;
        for (int i = 0; i < cache.size(); ++i)
            if (cache.get(i).getId() == id) index = i;

        IOQueues.Send.kill(id);
        cache.get(index).close();
        cache.remove(index);
    }

    public static SSLSocket[] getAllSockets () {

        SSLSocket[] returnValue = new SSLSocket[cache.size()];
        for (int i = 0; i < cache.size(); ++i) returnValue[i] = cache.get(i).socket;

        return returnValue;
    }

    public static int size () { return cache.size(); }
    public static OutputStream getStreamByIndex (int i) { return cache.get(i).stream; }
    public static String getNameByIndex (int i) { return cache.get(i).getName(); }
    public static String getNameById (int id) { return cache.get(getIndexById(id)).getName(); }
    public static int getIdByIndex (int i) { return cache.get(i).getId(); }
    public static int getIndexById (int id) {
        for (int i = 0; i < cache.size(); ++i)
            if (cache.get(i).getId() == id) return i; return -1;
    }

    public static class Incoming {

        private static Object mutex = new Object();

        private static Queue<SSLSocket> incomingClients = new LinkedList<SSLSocket>();
        public static void enqueue (SSLSocket socket) {
            synchronized (mutex) { incomingClients.add(socket); }
        }
        public static SSLSocket dequeue () {
            synchronized (mutex) { return incomingClients.remove(); }
        }
        public static int count () {
            synchronized (mutex) { return incomingClients.size(); }
        }
    }

    public static class Disconnecting {

        private static Object mutex = new Object();

        private static Queue<Integer> disconnectingClients = new LinkedList<Integer>();
        public static void enqueue (int id) {
            synchronized (mutex) { disconnectingClients.add(id); }
        }
        public static int dequeue () {
            synchronized (mutex) { return disconnectingClients.remove(); }
        }
        public static int count () {
            synchronized (mutex) { return disconnectingClients.size(); }
        }
    }

    public static class IOQueues {

        public static class Send {

            private static HashMap<Integer, Queue<Byte>> sendQueues = new HashMap<Integer, Queue<Byte>>();

            public static void queue (int id, byte[] data) { synchronized (sendQueues) {

                for (byte b : data) sendQueues.get(id).add(b);
            } }
            public static void queueAll (byte[] data) { synchronized (sendQueues) {

                sendQueues.keySet().forEach(id -> {
                    for (byte b : data) sendQueues.get(id).add(b);
                });
            } }
            public static boolean isEmpty (int id) { synchronized (sendQueues) {

                return sendQueues.get(id).size() == 0;
            } }
            public static byte pop (int id) { synchronized (sendQueues) {

                return sendQueues.get(id).remove();
            } }

            public static void init (int id) { synchronized (sendQueues) {

                if (!sendQueues.containsKey(id)) sendQueues.put(id, new LinkedList<Byte>());
            } }
            public static void kill (int id) { synchronized (sendQueues) {

                if (sendQueues.containsKey(id)) {
                    sendQueues.get(id).clear(); sendQueues.remove(id);
                }
            } }
        }

        public static class Recv {

            private static Queue<Packet> recvQueue = new LinkedList<Packet>();

            public static void queue (Packet packet) { synchronized (recvQueue) {

                recvQueue.add(packet);
            } }
            public static boolean isEmpty () { synchronized (recvQueue) {

                return recvQueue.size() == 0;
            } }
            public static Packet pop () { synchronized (recvQueue) {

                return recvQueue.remove();
            } }
        }
    }
}
