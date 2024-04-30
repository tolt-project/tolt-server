
package tolt.server.core.commands;
import tolt.server.core.commands.Command;

import tolt.server.system.Action;

public class StopCommand extends Command {

    @Override
    public String[] getNames () { return new String[] {
        "exit", "stop", "close", "quit"
    }; }

    @Override
    public String[] getManual () { return new String[] {
        "Calls shutdown event and terminates the process."
    }; }

    @Override
    public void execute (String[] args) {

        Action.shutdown(0, "Stop command was encountered.");
    }
}
