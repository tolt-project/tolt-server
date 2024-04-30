
package tolt.server.core;

import java.util.Arrays;
import java.util.Vector;

import tolt.server.system.Action;
import tolt.server.service.logging.Logging;
import tolt.server.core.commands.*;

public class Console {

    public static void load () {
    }

    public static void parse (String input) {

        String[] args = input.contains(" ") ? input.split(" ") : new String[]{input};

        switch (args[0]) {

            case "exit": case "close": case "quit": case "stop":
                Action.shutdown(0, "Stop command was encountered."); break;

            case "connections": case "conn":

            default: Logging.err("Unknown command: '" + input + "'!"); break;
        }
    }
}
