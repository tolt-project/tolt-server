
package tolt.server.system;

import tolt.server.service.logging.Logging;

public class Action {

    public static void shutdown (int exitCode) {

        shutdown(exitCode, "No reason specified.");
    }
    public static void shutdown (int exitCode, String reason) {

        Logging.log("Shutting down Tolt Server. " + reason);

        Logging.log("Exit: " + String.valueOf(exitCode));
        System.exit(exitCode);
    }
}
