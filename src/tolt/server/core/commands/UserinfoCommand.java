
package tolt.server.core.commands;
import tolt.server.core.commands.Command;

import tolt.server.database.Database;
import tolt.server.database.userbase.UserEntry;
import tolt.server.security.util.SHAWrapper;
import tolt.server.service.logging.Logging;

public class UserinfoCommand extends Command {

    @Override
    public String[] getNames () { return new String[] {
        "userinfo", "ui", "uinfo"
    }; }

    @Override
    public String[] getManual () { return new String[] {
        "Shows information about a specified User.",
        "Usage: uinfo john",
    }; }

    @Override
    public void execute (String[] args) {

        if (args.length != 2) return;

        for (var userHash : Database.User.getAllUserHashes())
            if (userHash.equals(SHAWrapper.sha256Text(args[1]))) {
                print(Database.User.get(userHash));
                return;
            }

        Logging.warn("User `" + args[1] + "' was not found!!");
    }

    private void print (UserEntry user) {

        System.out.println("Username: " + user.username);
        System.out.println("displayName: " + user.displayName);
        System.out.println("userHash: " + user.userHash);
        System.out.println("userId: " + user.userId);
            System.out.println("realName: " + user.realName);
            System.out.println("emailAddress: " + user.emailAddress);
        System.out.println("registrationTimeStamp: " + user.registrationTimeStamp);
        System.out.println("lastLoginTimeStamp: " + user.lastLoginTimeStamp);
        System.out.println("registrationIPA: " + user.registrationIPA);
        System.out.println("lastLoginIPA: " + user.lastLoginIPA);
            System.out.println("loginCount: " + user.loginCount);
            System.out.println("messagesSent: " + user.messagesSent);
    }
}
