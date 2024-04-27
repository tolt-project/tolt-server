
package tolt.server;

import tolt.server.network.Network;
import tolt.server.service.logging.Logging;
import tolt.server.service.Config;
import tolt.server.service.stats.Stats;
import tolt.server.system.Action;

public class Main {

    public static void main (String[] args) {

        Logging.start();
        Config.load();
        Stats.load();
        Logging.log(String.format(
            "Server has been started %d times.",
            Stats.increment("server.launch-count")
        ));

        Network.start();

        try { System.in.read(); } catch (Exception e) {}

        Network.stop();

        Action.shutdown(0, "Process ended.");
    }
}
