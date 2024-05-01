
package tolt.server.database.channelbase;

import java.io.File;
import java.nio.file.Files;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import tolt.server.security.util.Hashing;
import tolt.server.security.util.SHAWrapper;
import tolt.server.security.util.AESWrapper;
import tolt.server.service.logging.Logging;
import tolt.server.service.Config;
import tolt.server.database.Idgen;

public class Channelbase {

    private static final String channelbasePath = "./db/chn";

    public static int tryCreateChannel (String channelName, String channelNameContext, String creationUsername) {

        String channelHash = SHAWrapper.sha256Text(channelNameContext + "/" + channelName);
        File channelFile = new File(channelbasePath + channelHash);
        if (channelFile.exists()) return -1;

        ChannelEntry entry = new ChannelEntry();

            entry.channelName = channelName;
            entry.channelHash = channelHash;
            entry.channelId = Idgen.generateId("chn");

            entry.channelNameContext = channelNameContext;

            entry.creationTimeStamp = System.currentTimeMillis() / 1000L;
            entry.creationUsername = creationUsername;

            entry.messageCount = 0;

        saveChannel(entry);

        return 0;
    }

    public static void saveChannel (ChannelEntry entry) { try {

        String channelHash = SHAWrapper.sha256Text(entry.channelNameContext + "/" + entry.channelName);
        File channelFile = new File(channelbasePath + channelHash);

        if (!channelFile.exists()) channelFile.getParentFile().mkdirs();

        byte[] key = Hashing.getHash32(channelHash.getBytes(StandardCharsets.UTF_8));
        byte[] iv = Hashing.getHash16(Config.getString("database.salt").getBytes(StandardCharsets.UTF_8));
        byte[] saveData = AESWrapper.encrypt(entry.serialize(), key, iv);

            Logging.debug("saved channel entry of size: " + entry.serialize().length);

        Files.write(channelFile.toPath(), saveData);

    } catch (Exception e) {

        Logging.warn("Failed to save Channel-entry: " + e.getMessage());
    } }

    public static ChannelEntry loadChannel (String channelHash) { try {

        File channelFile = new File(channelbasePath + channelHash);
        if (!channelFile.exists()) return null;

        byte[] key = Hashing.getHash32(channelHash.getBytes(StandardCharsets.UTF_8));
        byte[] iv = Hashing.getHash16(Config.getString("database.salt").getBytes(StandardCharsets.UTF_8));

        byte[] loadData = Files.readAllBytes(channelFile.toPath());

            Logging.debug("loaded channel entry of size: " + AESWrapper.decrypt(loadData, key, iv).length);

        return new ChannelEntry(AESWrapper.decrypt(loadData, key, iv));

    } catch (Exception e) {

        Logging.warn("Failed to load Channel-entry: " + e.getMessage());
        return null;
    } }

    public static boolean channelExists (String channelHash) {

        return new File(channelbasePath + channelHash).exists();
    }
}
