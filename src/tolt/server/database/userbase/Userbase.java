
package tolt.server.database.userbase;

import java.io.File;
import java.nio.file.Files;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Vector;

import tolt.server.security.util.Hashing;
import tolt.server.security.util.SHAWrapper;
import tolt.server.security.util.AESWrapper;
import tolt.server.service.logging.Logging;
import tolt.server.service.Config;
import tolt.server.database.Idgen;

public class Userbase {

    private static final String userbasePath = "./db/usr/";

    public static int tryCreateUser (
        String username,
        String passwordHash,
        String realName,
        String emailAddress,
        String requesterIPA
    ) {

        String userHash = SHAWrapper.sha256Text(username);
        File userFile = new File(userbasePath + userHash);
        if (userFile.exists()) return -1; //user already exists

        UserEntry entry = new UserEntry();

            entry.username = username;
            entry.displayName = username;
            entry.userHash = userHash;
            entry.userId = Idgen.generateId("usr");

            entry.realName = realName;
            entry.emailAddress = emailAddress;
            entry.passwordHash = passwordHash;

            entry.registrationTimeStamp = System.currentTimeMillis() / 1000L;
            entry.lastLoginTimeStamp = System.currentTimeMillis() / 1000L;
            entry.registrationIPA = requesterIPA;
            entry.lastLoginIPA = requesterIPA;

            entry.loginCount = 1;
            entry.messagesSent = 0;

        saveUser(entry);

        return 0; // successfully created da user
    }

    public static void saveUser (UserEntry entry) { try {

        String userHash = SHAWrapper.sha256Text(entry.username);
        File userFile = new File(userbasePath + userHash);

        if (!userFile.exists()) userFile.getParentFile().mkdirs();

        byte[] key = Hashing.getHash32(userHash.getBytes(StandardCharsets.UTF_8));
        byte[] iv = Hashing.getHash16(Config.getString("database.salt").getBytes(StandardCharsets.UTF_8));
        byte[] saveData = AESWrapper.encrypt(entry.serialize(), key, iv);

            Logging.debug("saved user entry of size: " + saveData.length);

        Files.write(userFile.toPath(), saveData);

    } catch (Exception e) {

        Logging.warn("Failed to save User-entry: " + e.getMessage());
    } }

    public static UserEntry loadUser (String userHash) { try {

        File userFile = new File(userbasePath + userHash);
        if (!userFile.exists()) return null;

        byte[] key = Hashing.getHash32(userHash.getBytes(StandardCharsets.UTF_8));
        byte[] iv = Hashing.getHash16(Config.getString("database.salt").getBytes(StandardCharsets.UTF_8));

        byte[] loadData = Files.readAllBytes(userFile.toPath());

            Logging.debug("loaded user entry of size: " + AESWrapper.decrypt(loadData, key, iv).length);

        return new UserEntry(AESWrapper.decrypt(loadData, key, iv));

    } catch (Exception e) {

        Logging.warn("Failed to load User-entry: " + e.getMessage());
        return null;
    } }

    public static boolean userExists (String userHash) {

        return new File(userbasePath + userHash).exists();
    }
}
