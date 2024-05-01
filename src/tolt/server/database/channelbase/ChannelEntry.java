
package tolt.server.database.channelbase;

import java.util.Vector;
import java.io.File;
import java.nio.file.Files;

import tolt.server.core.module.Message;
import tolt.server.service.util.BufferBuilder;
import tolt.server.security.util.Hashing;
import tolt.server.security.util.SHAWrapper;
import tolt.server.security.util.AESWrapper;
import tolt.server.service.logging.Logging;

public class ChannelEntry {

    // entry values (serialize)

        // channel details
        public String channelName;
        public String channelHash;
        public long channelId;

        // channel information
        public String channelNameContext;

        // channel trivia
        public long creationTimeStamp;
        public String creationUsername;

        // channel stats
        public long messageCount;

    // runtime values (DONT serialize)

        public boolean modified = false;


    public ChannelEntry () {}
    public ChannelEntry (byte[] data) {

        BufferBuilder builder = new BufferBuilder(data);

        channelName = builder.getString();
        channelHash = builder.getString();
        channelId = builder.getLong();
            channelNameContext = builder.getString();
        creationTimeStamp = builder.getLong();
        creationUsername = builder.getString();
            messageCount = builder.getLong();
    }

    public byte[] serialize () {

        BufferBuilder builder = new BufferBuilder();

        builder.append(channelName);
        builder.append(channelHash);
        builder.append(channelId);
            builder.append(channelNameContext);
        builder.append(creationTimeStamp);
        builder.append(creationUsername);
            builder.append(messageCount);

        return builder.toArray();
    }

    // channel data (serialize seperately)

    private static final String dbPath = "db/content/";
    private static final int maxSectionSize = 1048576; //1mb

    public Vector<Message> messageCache = new Vector<Message>();

    public void save () {

        modified = false;

        try {

            BufferBuilder builder = new BufferBuilder();
            builder.append(messageCache.size());
            for (Message message : messageCache)
                builder.append(message.serialize());

            int c = -1;
            File file = new File(dbPath + channelHash + "/" + (c++) + ".dat");
            while (file.length() > maxSectionSize)
                file = new File(dbPath + channelHash + "/" + (c++) + ".dat");

            if (!file.exists()) file.getParentFile().mkdirs();

            Files.write(file.toPath(), builder.toArray());

                Logging.debug("saved " + builder.size() + " bytes to " + file.getPath());

        } catch (Exception e) {

            Logging.warn("Failed to save channel: " + e.getMessage());
        }
    }
    public void load () {

        try {

            int c = -1;
            File file = new File(dbPath + channelHash + "/" + (c++) + ".dat");
            while (true)
                if (new File(dbPath + channelHash + "/" + (c + 1) + ".dat").exists())
                    file = new File(dbPath + channelHash + "/" + (c++) + ".dat");
                else break;

            messageCache.clear();
            BufferBuilder builder = new BufferBuilder(Files.readAllBytes(file.toPath()));
            for (int i = 0; i < builder.getInt(); ++i)
                messageCache.add(new Message(builder.getByteArray()));

                Logging.debug("loaded " + builder.size() + " bytes from " + file.getPath());

        } catch (Exception e) {

            Logging.warn("Failed to save channel: " + e.getMessage());
        }
    }
}
