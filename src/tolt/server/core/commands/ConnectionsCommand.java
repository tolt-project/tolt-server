
package tolt.server.core.commands;
import tolt.server.core.commands.Command;

import tolt.server.core.cache.SessionCache;
import tolt.server.core.cache.SessionCacheEntry;
import tolt.server.service.logging.Logging;

public class ConnectionsCommand extends Command {

    @Override
    public String[] getNames () { return new String[] {
        "connections", "conn", "list", "table"
    }; }

    @Override
    public String[] getManual () { return new String[] {
        "Shows a list/table of all currently connected",
        "clients."
    }; }

    @Override
    public void execute (String[] args) {

        SessionCacheEntry[] entries = SessionCache.getAll();

        System.out.println(
            entries.length == 1
            ? "There is currently `1' active connection:"
            : "There are currently `" + entries.length + "' active connections:"
        );

        for (var entry : entries)
            System.out.println(String.format(
                "| %d | %s | %s |",
                entry.id, entry.fullRemoteIPA, entry.username
            ));
    }
}
