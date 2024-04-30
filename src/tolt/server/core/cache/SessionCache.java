
package tolt.server.core.cache;

import java.util.Vector;

public class SessionCache {

    private static Vector<SessionCacheEntry> cache = new Vector<SessionCacheEntry>();

    public static SessionCacheEntry getByUsername (String username) { synchronized (cache) {

        for (var entry : cache) if (entry.username.equals(username)) return entry; return null;
    } }
    public static SessionCacheEntry getByUserHash (String userHash) { synchronized (cache) {

        for (var entry : cache) if (entry.userHash.equals(userHash)) return entry; return null;
    } }
    public static SessionCacheEntry getById (int id) { synchronized (cache) {

        for (var entry : cache) if (entry.id == id) return entry; return null;
    } }
    public static SessionCacheEntry[] getAll () { synchronized (cache) {

        return cache.toArray(new SessionCacheEntry[cache.size()]);
    } }

    public static void set (SessionCacheEntry entry) { synchronized (cache) {

        if (cache.contains(entry)) cache.remove(entry);
        cache.add(entry);
    } }
}
