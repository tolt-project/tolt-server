
package tolt.server.network;

import javax.net.ssl.SSLSocket;

import tolt.server.service.logging.Logging;
import tolt.server.service.stats.Stats;

public class Authentication {

    public static void Handle (SSLSocket socket) {

        //thats a bot filter check template from another codebase
        //it needs to be implemented here along with login authentication stuff
        //TODO lol

            // byte[] nb = new byte[4];
            // socket.getInputStream().read(nb, 0, 4);
            // if (nb[0]!=107||nb[1]!=105||nb[2]!=114||nb[3]!=97) {
            //
            //     Logging.log(socket.getRemoteSocketAddress().toString() +
            //         " failed to pass Bot-filter; Connection dropped.");
            //     socket.close();
            //     Stats.increment("catching.bots-dropped");
            //
            // } else {
            //
            //     // Handling.addClient(socket);
            // }
    }
}
