
package tolt.server.system;

import java.time.Instant;
import java.time.Duration;

import tolt.server.network.Network;
import tolt.server.network.Handling;
import tolt.server.service.logging.Logging;
import tolt.server.service.Config;
import tolt.server.service.stats.Stats;
import tolt.server.core.PacketProcessor;

public class Action {

    private static boolean startupCalled = false;
    public static void start () {

        if (startupCalled) {
            Logging.err("Startup called twice!"); return;
        } startupCalled = true;

        Instant timeStart = Instant.now();

        Event.onStartup();
        Stats.increment("startup-count");

        Instant timeStop = Instant.now();
        Duration duration = Duration.between(timeStart, timeStop);
        long startMillis = duration.toMillis();

        Logging.success(String.format(
            "Server started successfully! Took %dms.", startMillis));
    }

    public static void shutdown (int exitCode) {

        shutdown(exitCode, "No reason specified.");
    }
    private static boolean shutdownCalled = false;
    public static void shutdown (int exitCode, String reason) {

        if (shutdownCalled) {
            Logging.err("Shutdown called twice!"); return;
        } shutdownCalled = true;

        Logging.log("Shutting down Tolt Server: " + reason);
        Event.onShutdown();

        Logging.log("Exit: " + String.valueOf(exitCode));
        System.exit(exitCode);
    }

    private static class Event {

        public static void onStartup () {

            Logging.start();
            Config.load();
            Stats.load();

            Network.start();
            Handling.start();
            PacketProcessor.start();
        }

        public static void onShutdown () {

            Network.stop();
            Handling.stop();
            PacketProcessor.stop();

            Stats.save();
        }
    }
}
