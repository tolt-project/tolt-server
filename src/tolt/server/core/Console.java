
package tolt.server.core;

import java.util.Arrays;
import java.util.Vector;

import tolt.server.system.Action;
import tolt.server.service.logging.Logging;
import tolt.server.core.commands.*;

public class Console {

    public static void load () {

        commands.add(new StopCommand());
        commands.add(new HelpCommand());
        commands.add(new ConnectionsCommand());

        Logging.log("Loaded `" + commands.size() + "' terminal commands.");
    }

    private static Vector<Command> commands = new Vector<Command>();

    public static void listCommands () {

        System.out.println();
        for (Command command : commands) {

            for (String name : command.getNames())
                System.out.print(name + ", ");
            System.out.println();

            for (String line : command.getManual())
                System.out.println("    " + line);
        }
    }

    public static void parse (String input) {

        String[] args = input.contains(" ") ? input.split(" ") : new String[]{input};

        for (Command command : commands)
            if (Arrays.stream(command.getNames()).anyMatch(args[0]::equals)) {
                command.execute(args); return;
            }

        Logging.err("Unknown command: `" + input + "'!");
    }
}
