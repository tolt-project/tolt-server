
package tolt.server.network;

import java.util.Vector;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;

import tolt.server.network.module.CacheEntry;

public class Cache {

    private static Vector<CacheEntry> cache = new Vector<CacheEntry>();

    public static int initEntry (SSLSocket socket) {

        cache.add(new CacheEntry(socket));
        return socket.hashCode();
    }

    public static SSLSocket[] getAllSockets () {

        SSLSocket[] returnValue = new SSLSocket[cache.size()];
        for (int i = 0; i < cache.size(); ++i) returnValue[i] = cache.get(i).socket;

        return returnValue;
    }

    public static int size () { return cache.size(); }
    public static OutputStream get (int i) { return cache.get(i).stream; }
}
