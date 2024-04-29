
package tolt.server.network;

import tolt.server.service.logging.Logging;
import tolt.server.system.Action;

public class Network {

    public static boolean isOnline ()
        { return Catching.isRunning(); }

    public static void start () {

        if (isOnline()) return;

        Catching.start();

        if (!isOnline()) {

            Logging.err("Listener failed to start! NOT RUNNING!");
            Action.shutdown(-1, "Failed to initialize Server.");
        }
    }

    public static void stop () {

        if (!isOnline()) return;

        Logging.log("Stopping the Listener..");
        Catching.stop();
    }
}
