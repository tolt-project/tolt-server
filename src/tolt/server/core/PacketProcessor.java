
package tolt.server.core;

import tolt.server.service.logging.Logging;
import tolt.server.network.cache.Cache;
import tolt.server.network.module.Packet;

public class PacketProcessor {

    public static void start () {

        shouldStop = false;
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

        while (true) try {

            if (Cache.IOQueues.Recv.isEmpty())
                Thread.sleep(10);

            else {

                Packet packet = Cache.IOQueues.Recv.pop();

                Logging.log(String.format(
                    "Packet of size %d entered processing...", packet.size()));

                Logging.warn("Discarded Packet due to nothing being implemented!");
            }

        } catch (Exception e) {

            Logging.stackErr(e);
        }
    }
}
