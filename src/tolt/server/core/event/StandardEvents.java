
package tolt.server.core.event;

import tolt.server.network.cache.Cache;
import tolt.server.security.util.SHAWrapper;
import tolt.server.service.util.BufferBuilder;
import tolt.server.service.logging.Logging;
import tolt.server.database.Database;
import tolt.server.core.module.Message;

public class StandardEvents {

    public static void joinChannelRequestEvent (byte[] data) {

        // BufferBuilder builder = new BufferBuilder(data);
        //
        // Database.Channel.create(builder.getString());
    }

    public static void messageChannelRequestEvent (byte[] data) {

        // BufferBuilder builder = new BufferBuilder(data);
        //
        // String channelHash
        //
        // Database.Channel.addMessage();

        Database.Channel.create("general", "global", "?");
        Database.Channel.addMessage(SHAWrapper.sha256Text("global/general"), new Message("?", new String(data, java.nio.charset.StandardCharsets.UTF_8)));
        for (Message message : Database.Channel.getMessages(SHAWrapper.sha256Text("global/general"), 69))
            Logging.debug(message.author + ": " + message.content);
    }

    public static void getMessagesFromChannelRequestEvent (byte[] data) {


    }
}
