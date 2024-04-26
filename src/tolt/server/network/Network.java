
package tolt.server.network;

import tolt.server.service.logging.Logging;
import tolt.server.system.Action;

public class Network {

    public static boolean isOnline ()
        { return Server.isRunning(); }

    public static void start () {

        if (isOnline()) return;

        Server.start();

        if (!isOnline()) {

            Logging.err("Server failed to start! NOT RUNNING!");
            Action.shutdown(-1, "Failed to initialize Server.");

        } else {

            Logging.success("Server started successfully!!");
        }
    }

    public static void stop () {

        if (!isOnline()) return;

        Logging.log("Stopping the Server..");
        Server.stop();
    }
}
