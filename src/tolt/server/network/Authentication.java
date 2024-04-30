
package tolt.server.network;

import java.io.InputStream;
import java.nio.ByteBuffer;

import tolt.server.service.logging.Logging;
import tolt.server.service.stats.Stats;
import tolt.server.network.cache.Cache;
import tolt.server.database.Database;

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

            nb = new byte[1];
            stream.read(nb, 0, 1);

            switch (nb[0]) {

                case 0: return register(id, stream);
                case 1: return login(id, stream);
                case 2: return token(id, stream);
                default: throw new Exception("Invalid authentication scheme: " + nb[0]);
            }

        } catch (Exception e) {

            Stats.increment("authentication.invalid");
            Logging.log(Cache.getNameById(id) +
                " failed to authenticate: " + e.getMessage());
        }

        return false;
    }

    private static boolean register (int id, InputStream stream) {

        try {

            String username = ""; {

                byte[] buffer = new byte[4];
                stream.read(buffer, 0, 4);
                int size = ByteBuffer.wrap(buffer).getInt();
                if (size > 1024) throw new Exception("username size buffer too large! 1024 < " + size);
                buffer = new byte[size];
                stream.read(buffer, 0, size);
                username = new String(buffer, "UTF8");
            }
            String passwordHash = ""; {

                byte[] buffer = new byte[4];
                stream.read(buffer, 0, 4);
                int size = ByteBuffer.wrap(buffer).getInt();
                if (size > 1024) throw new Exception("passwordHash size buffer too large! 1024 < " + size);
                buffer = new byte[size];
                stream.read(buffer, 0, size);
                passwordHash = new String(buffer, "UTF8");
            }
            String realName = ""; {

                byte[] buffer = new byte[4];
                stream.read(buffer, 0, 4);
                int size = ByteBuffer.wrap(buffer).getInt();
                if (size > 1024) throw new Exception("realName size buffer too large! 1024 < " + size);
                buffer = new byte[size];
                stream.read(buffer, 0, size);
                realName = new String(buffer, "UTF8");
            }
            String emailAddress = ""; {

                byte[] buffer = new byte[4];
                stream.read(buffer, 0, 4);
                int size = ByteBuffer.wrap(buffer).getInt();
                if (size > 1024) throw new Exception("emailAddress size buffer too large! 1024 < " + size);
                buffer = new byte[size];
                stream.read(buffer, 0, size);
                emailAddress = new String(buffer, "UTF8");
            }
            String requesterIPA = Cache.getNameById(id).split(":")[0].substring(1);

            switch (Database.User.register(
                username, passwordHash, realName, emailAddress, requesterIPA
            )) {
                case 0: Logging.log(id + ": registered as '" + username + "'."); return true;
                case -1: throw new Exception("the username is '" + username + "' taken!");
                default: throw new Exception("Userbase.tryCreateUser() returned an unknown error code.");
            }

        } catch (Exception e) {

            Stats.increment("authentication.invalid-register");
            Logging.log(Cache.getNameById(id) +
                " failed to register: " + e.getMessage());
            return false;
        }
    }
    private static boolean login (int id, InputStream stream) {

        try {

            String username = ""; {

                byte[] buffer = new byte[4];
                stream.read(buffer, 0, 4);
                int size = ByteBuffer.wrap(buffer).getInt();
                if (size > 1024) throw new Exception("username size buffer too large! 1024 < " + size);
                buffer = new byte[size];
                stream.read(buffer, 0, size);
                username = new String(buffer, "UTF8");
            }
            String passwordHash = ""; {

                byte[] buffer = new byte[4];
                stream.read(buffer, 0, 4);
                int size = ByteBuffer.wrap(buffer).getInt();
                if (size > 1024) throw new Exception("passwordHash size buffer too large! 1024 < " + size);
                buffer = new byte[size];
                stream.read(buffer, 0, size);
                passwordHash = new String(buffer, "UTF8");
            }
            String requesterIPA = Cache.getNameById(id).split(":")[0].substring(1);

            switch (Database.User.login(
                username, passwordHash, requesterIPA
            )) {
                case 0: Logging.log(id + ": logged in as '" + username + "'."); return true;
                case -1: throw new Exception("there is no such user as '" + username + "' in the database!");
                case -2: throw new Exception("Herobrine does't want '" + username + "' to log in right now!");
                case -3: throw new Exception("password mismatch for '" + username + "'!");
                default: throw new Exception("Userbase.login() returned an unknown error code.");
            }

        } catch (Exception e) {

            Stats.increment("authentication.invalid-login");
            Logging.log(Cache.getNameById(id) +
                " failed to register: " + e.getMessage());
            return false;
        }
    }
    private static boolean token (int id, InputStream stream) {
        Logging.err("Token login has not yet been implemented!!");
        return false;
        //recv info to login with a token
    }
}
