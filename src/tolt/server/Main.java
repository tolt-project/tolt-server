
package tolt.server;

import tolt.server.system.Action;
import tolt.server.core.Command;
import tolt.server.service.logging.Logging;

public class Main {

    public static void main (String[] args) {

        Action.start();

        while (true) try {

            String input = System.console().readLine();
            Command.parse(input);

        } catch (Exception e) {

            Logging.stackErr(e);
        }
    }
}
