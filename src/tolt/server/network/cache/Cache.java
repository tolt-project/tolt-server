
package tolt.server.network.cache;

import java.util.Queue;
import java.util.LinkedList;
import java.util.Vector;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;

public class Cache {

    private static Vector<CacheEntry> cache = new Vector<CacheEntry>();

    public static int initEntry (SSLSocket socket) {

        cache.add(new CacheEntry(socket));
        return socket.hashCode();
    }

    public static void killEntry (int id) {

        int index = -1;
        for (int i = 0; i < cache.size(); ++i)
            if (cache.get(i).getId() == id) index = i;

        cache.get(index).close();
        cache.remove(index);
    }

    public static SSLSocket[] getAllSockets () {

        SSLSocket[] returnValue = new SSLSocket[cache.size()];
        for (int i = 0; i < cache.size(); ++i) returnValue[i] = cache.get(i).socket;

        return returnValue;
    }

    public static int size () { return cache.size(); }
    public static OutputStream get (int i) { return cache.get(i).stream; }

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
}
