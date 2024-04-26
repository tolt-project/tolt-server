
package tolt.server.network;

import tolt.server.service.Logging;

public class Network {

    public static boolean isOnline ()
        { return Server.isRunning(); }

    public static void start () {

        if (isOnline()) return;

        Server.start();

        if (!isOnline()) {

            Logging.log("Server failed to start! NOT RUNNING!");
        }
    }

    public static void stop () {

        if (!isOnline()) return;

        Server.stop();
    }
}
