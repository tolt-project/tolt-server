
package tolt.server.core;

import tolt.server.system.Action;
import tolt.server.service.logging.Logging;

public class Command {

    public static void parse (String input) {

        String[] args = input.contains(" ") ? input.split(" ") : new String[]{input};

        switch (args[0]) {

            case "exit": case "close": case "quit": case "stop":
                Action.shutdown(0, "Stop command was encountered."); break;

            default: Logging.err("Unknown command: '" + input + "'!"); break;
        }
    }
}
