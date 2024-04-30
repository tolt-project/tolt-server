
package tolt.server.core.cache;

import tolt.server.security.util.SHAWrapper;

public class SessionCacheEntry {

    public String username;
    public String userHash;
    public String fullRemoteIPA;
    public String remoteIPA;
    public int id;

    public SessionCacheEntry (String username, String fullRemoteIPA, int id) {

        this.username = username;
        this.userHash = SHAWrapper.sha256Text(username);
        this.fullRemoteIPA = fullRemoteIPA;
        this.remoteIPA = fullRemoteIPA.split(":")[0].substring(1);;
        this.id = id;
    }
}
