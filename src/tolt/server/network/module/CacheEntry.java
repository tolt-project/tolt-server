
package tolt.server.network.module;

import java.io.OutputStream;
import javax.net.ssl.SSLSocket;

public class CacheEntry {

    public SSLSocket socket;
    public OutputStream stream;

    public int getId () { return socket.hashCode(); }

    public CacheEntry (SSLSocket socket) { try {

        this.socket = socket;
        this.stream = socket.getOutputStream();

    } catch (Exception e) {} }
}
