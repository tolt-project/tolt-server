
package tolt.server.core.event;

import java.nio.ByteBuffer;

import tolt.server.network.cache.Cache;

public class DebugEvents {

    public static void debugRelayEvent (byte[] data) {

        ByteBuffer buffer = ByteBuffer.allocate(data.length + 6);
        buffer.putShort((short)69);
        buffer.putInt(data.length);
        buffer.put(data);
        Cache.IOQueues.Send.queueAll(buffer.array());
    }
}
