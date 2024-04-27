
package tolt.server;

import tolt.server.system.Action;

public class Main {

    public static void main (String[] args) {

        Action.start();

        try { System.in.read(); } catch (Exception e) {}

        Action.shutdown(0, "Process ended.");
    }
}
