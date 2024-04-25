
package tolt.server.network;

public class Network {

    public static boolean isOnline ()
        { return Server.isRunning(); }

    public static void start () {

        if (isOnline()) return;

        Server.start();

        if (!isOnline()) {

            System.out.println("Server failed to start! NOT RUNNING!");
        }
    }

    public static void stop () {

        if (!isOnline()) return;

        Server.stop();
    }
}
