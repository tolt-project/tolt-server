
package tolt.server;

import tolt.server.network.Network;
import tolt.server.service.logging.Logging;

public class Main {

    public static void main (String[] args) {

        Logging.start();

        Network.start();

        try { System.in.read(); } catch (Exception e) {}

        Network.stop();
    }
}
