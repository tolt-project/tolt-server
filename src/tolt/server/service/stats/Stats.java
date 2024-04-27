
package tolt.server.service.stats;

import java.util.HashMap;
import java.io.File;
import java.nio.file.Files;

import tolt.server.service.logging.Logging;

public class Stats {

    static private HashMap<String, Integer> stats = new HashMap<String, Integer>();

    public static int get (String key) {

        return stats.containsKey(key) ? stats.get(key) : 0;
    }

    public static void set (String key, int newValue) {

        stats.put(key, newValue);
    }
    public static int increment (String key) {

        if (!stats.containsKey(key)) stats.put(key, 0);
        stats.put(key, stats.get(key) + 1);

        return stats.get(key);
    }


    private static String statPath = "statistics.dat";
    public static void load () {

        File statFile = new File(statPath);
        if (!statFile.exists()) return;

        try {

            for (String line : Files.readAllLines(statFile.toPath())) {

                if (line.isEmpty() || !line.contains(":")) continue;

                String[] values = line.split(":");
                stats.put(values[0], Integer.parseInt(values[1]));
            }

        } catch (Exception e) { Logging.stackWarn(e); }
    }
    public static void save () {

        File statFile = new File(statPath);
        if (statFile.exists()) statFile.delete();

        String writeCache = "";
        for (String key : stats.keySet().toArray(new String[stats.size()]))
            writeCache += "\n" + key + ":" + String.valueOf(stats.get(key));

        try {
            Files.writeString(statFile.toPath(), writeCache);

        } catch (Exception e) { Logging.stackWarn(e); }
    }
}
