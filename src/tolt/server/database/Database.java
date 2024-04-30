
package tolt.server.database;

import java.util.Vector;

import tolt.server.service.logging.Logging;
import tolt.server.security.util.SHAWrapper;
import tolt.server.database.userbase.Userbase;
import tolt.server.database.userbase.UserEntry;

public class Database {

    public static Object mutex = new Object();

    private static int counter = 0;
    public static void tick () {

        counter++;
        if (counter == 10) { counter = 0;

            User.tick();
        }
    }
    public static void saveCache () { }

    public static class User {

        private static Vector<UserEntry> entryCache = new Vector<UserEntry>();
        private static Vector<Long> accessStamps = new Vector<Long>();

        public static UserEntry get (String userHash) { synchronized (Database.mutex) {
            for (int i = 0; i < entryCache.size(); ++i)
                if (entryCache.get(i).userHash.equals(userHash)) {
                    accessStamps.set(i, System.currentTimeMillis() / 1000L);
                    return entryCache.get(i);
                }
            return load(userHash);
        } }
        public static void set (UserEntry newEntry) { synchronized (Database.mutex) {
            for (int i = 0; i < entryCache.size(); ++i)
                if (entryCache.get(i).userHash.equals(newEntry.userHash)) {
                    accessStamps.set(i, System.currentTimeMillis() / 1000L);
                    entryCache.set(i, newEntry);
                    save(newEntry.userHash);
                }
        } }

        public static void tick () { synchronized (Database.mutex) {
            cacheCheck();
        } }

        private static UserEntry load (String userHash) {
            UserEntry entry = Userbase.loadUser(userHash);
            accessStamps.add(System.currentTimeMillis() / 1000L);
            entryCache.add(entry);
            Logging.debug("loaded " + userHash);
            return entry;
        }
        private static void save (String userHash) {
            for (var entry : entryCache) if (entry.userHash.equals(userHash))
                Userbase.saveUser(entry);
        }
        private static void save () {
            for (var entry : entryCache) Userbase.saveUser(entry);
        }
        private static void cacheCheck () {
            Vector<String> removeList = new Vector<String>();
            for (int i = 0; i < accessStamps.size(); ++i)
                if ((System.currentTimeMillis() / 1000L) - accessStamps.get(i) > 60)
                    removeList.add(entryCache.get(i).userHash);
            for (String hash : removeList)
                for (int i = 0; i < entryCache.size(); ++i)
                    if (entryCache.get(i).userHash.equals(hash)) {
                        accessStamps.remove(i); entryCache.remove(i);
                        Logging.debug("unloaded " + hash);
                    }
        }

        public static int register (
            String username,
            String passwordHash,
            String realName,
            String emailAddress,
            String requesterIPA
        ) { synchronized (Database.mutex) {
            return Userbase.tryCreateUser(
                username, passwordHash, realName, emailAddress, requesterIPA
            );
        } }
        public static int login (
            String username,
            String passwordHash,
            String requesterIPA
        ) { synchronized (Database.mutex) {
            if (!Userbase.userExists(SHAWrapper.sha256Text(username))) return -1;
            UserEntry user = load(SHAWrapper.sha256Text(username)); if (user == null) return -2;
            if (!user.passwordHash.equals(passwordHash)) return -3;
            user.loginCount++;
            user.lastLoginTimeStamp = System.currentTimeMillis() / 1000L;
            user.lastLoginIPA = requesterIPA;
            return 0;
        } }
    }
}
