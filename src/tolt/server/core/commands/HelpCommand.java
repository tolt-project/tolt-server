
package tolt.server.core.commands;
import tolt.server.core.commands.Command;

import tolt.server.core.Console;

public class HelpCommand extends Command {

    @Override
    public String[] getNames () { return new String[] { "help", "?" }; }

    @Override
    public String[] getManual () { return new String[] {
        "Prints the help page; Prints this page."
    }; }

    @Override
    public void execute (String[] args) {

        Console.listCommands();
    }
}
