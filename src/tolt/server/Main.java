
package tolt.server;

import tolt.server.network.Network;

public class Main {

    public static void main (String[] args) {

        Network.start();

        try { System.in.read(); } catch (Exception e) {}

        Network.stop();
    }
}
