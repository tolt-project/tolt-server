
package tolt.server.core;

import tolt.server.system.Action;
import tolt.server.service.logging.Logging;

public class Core {

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

        Logging.log("Started Core-loop.");

        while (!shouldStop) try {

            Thread.sleep(100);
            Action.tick();

        } catch (Exception e) {

            Logging.stackErr(e);
        }

        Logging.log("Core-loop has stopped.");
        running = false;
    }
}
