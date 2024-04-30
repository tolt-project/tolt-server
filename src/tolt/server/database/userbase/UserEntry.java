
package tolt.server.database.userbase;

import tolt.server.service.util.BufferBuilder;

public class UserEntry {

    // account details
    public String username;
    public String displayName;
    public String userHash;
    public long userId;

    // account information
    public String realName;
    public String emailAddress;
    public String passwordHash;

    // account trivia
    public long registrationTimeStamp;
    public long lastLoginTimeStamp;
    public String registrationIPA;
    public String lastLoginIPA;

    // account stats
    public long loginCount;
    public long messagesSent;


    public UserEntry () {}
    public UserEntry (byte[] data) {

        BufferBuilder builder = new BufferBuilder(data);

        username = builder.getString();
        displayName = builder.getString();
        userHash = builder.getString();
        userId = builder.getLong();
            realName = builder.getString();
            emailAddress = builder.getString();
            passwordHash = builder.getString();
        registrationTimeStamp = builder.getLong();
        lastLoginTimeStamp = builder.getLong();
        registrationIPA = builder.getString();
        lastLoginIPA = builder.getString();
            loginCount = builder.getLong();
            messagesSent = builder.getLong();
    }

    public byte[] serialize () {

        BufferBuilder builder = new BufferBuilder();

        builder.append(username);
        builder.append(displayName);
        builder.append(userHash);
        builder.append(userId);
            builder.append(realName);
            builder.append(emailAddress);
            builder.append(passwordHash);
        builder.append(registrationTimeStamp);
        builder.append(lastLoginTimeStamp);
        builder.append(registrationIPA);
        builder.append(lastLoginIPA);
            builder.append(loginCount);
            builder.append(messagesSent);

        return builder.toArray();
    }
}
