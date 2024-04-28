
package tolt.server.network;

import java.io.InputStream;

import tolt.server.service.logging.Logging;
import tolt.server.service.stats.Stats;
import tolt.server.network.cache.Cache;

public class Authentication {

    public static boolean authenticate (int id, InputStream stream) {

        try {

            byte[] nb = new byte[4];
            stream.read(nb, 0, 4);
            if (nb[0]!=107||nb[1]!=105||nb[2]!=114||nb[3]!=97) {

                Stats.increment("authentication.bots-dropped");
                Logging.log(Cache.getNameById(id) +
                    " failed to pass Bot-filter; Connection dropped.");

                return false;
            }

        } catch (Exception e) {

            Stats.increment("authentication.invalid");
            Logging.log(Cache.getNameById(id) +
                " failed to authenticate: " + e.getMessage());

            return false;
        }

        return true;
    }
}
