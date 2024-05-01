
package tolt.server.core.module;

import tolt.server.service.util.BufferBuilder;

public class Message {

    public String author;
    public String content;
    public long channelId;


    public Message () {}
    public Message (String author, String content) {

        this.author = author;
        this.content = content;
    }
    public Message (byte[] data) {

        BufferBuilder builder = new BufferBuilder(data);

        System.out.println(builder.toString());

        builder.getByte();
        author = builder.getString();
        content = builder.getString();
        channelId = builder.getLong();
    }

    public byte[] serialize () {

        BufferBuilder builder = new BufferBuilder();

        builder.append((byte)0);
        builder.append(author);
        builder.append(content);
        builder.append(channelId);

        return builder.toArray();
    }
}
