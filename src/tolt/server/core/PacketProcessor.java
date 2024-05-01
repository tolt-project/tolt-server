
package tolt.server.core;

import java.nio.ByteBuffer;

import tolt.server.service.logging.Logging;
import tolt.server.network.cache.Cache;
import tolt.server.network.module.Packet;
import tolt.server.core.event.*;

public class PacketProcessor {

    public static void start () {

        shouldStop = false; running = true;
        new Thread () {
            public void run () { loop(); }
        }.start();
    }
    public static void stop () {

        shouldStop = true;
        while (running) try { Thread.sleep(10);
        } catch (Exception e) {}
    }

    public static boolean running, shouldStop;

    private static void loop () {

        Logging.log("Started Packet-processor.");

        while (!shouldStop) try {

            if (Cache.IOQueues.Recv.isEmpty())
                Thread.sleep(10);

            else {

                Packet packet = Cache.IOQueues.Recv.pop();

                Logging.debug(String.format(
                    "Packet of size %d entered processing...", packet.size()));

                switch (packet.id) {

                    case 69: DebugEvents.debugRelayEvent(packet.data); break;

                    case 0: StandardEvents.joinChannelRequestEvent(packet.data); break;
                    case 1: StandardEvents.messageChannelRequestEvent(packet.data); break;
                    case 2: StandardEvents.getMessagesFromChannelRequestEvent(packet.data); break;
                }
            }

        } catch (Exception e) {

            Logging.stackErr(e);
        }

        Logging.log("Packet-processor has stopped.");
        running = false;
    }
}
