
package tolt.server.database.channelbase;

import tolt.server.service.util.BufferBuilder;

public class ChannelEntry {

    // channel details
    public String channelName;
    public String channelHash;
    public long channelId;

    // channel information
    public String nameContext;

    // channel trivia
    public long creationTimeStamp;
    public String creationUsername;

    // channel stats
    public long messageCount;


    public ChannelEntry () {}
    public ChannelEntry (byte[] data) {

        BufferBuilder builder = new BufferBuilder(data);

        channelName = builder.getString();
        channelHash = builder.getString();
        channelId = builder.getLong();
            nameContext = builder.getString();
        creationTimeStamp = builder.getLong();
        creationUsername = builder.getString();
            messageCount = builder.getLong();
    }

    public byte[] serialize () {

        BufferBuilder builder = new BufferBuilder();

        builder.append(channelName);
        builder.append(channelHash);
        builder.append(channelId);
            builder.append(nameContext);
        builder.append(creationTimeStamp);
        builder.append(creationUsername);
            builder.append(messageCount);

        return builder.toArray();
    }
}
