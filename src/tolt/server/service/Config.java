
package tolt.server.service;

import java.util.HashMap;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import tolt.server.service.logging.Logging;

public class Config {

    private static HashMap<String, String> values
        = new HashMap<String, String>();

    public static String getString (String key) { return getString(key, ""); }
    public static String getString (String key, String _default) {
        return values.containsKey(key) ? values.get(key) : _default;
    }
    public static int getInt (String key) { return getInt(key, 0); }
    public static int getInt (String key, int _default) {
        try {
            return values.containsKey(key) ? Integer.parseInt(values.get(key)) : _default;
        } catch (Exception e) { return _default; }
    }
    public static boolean getBool (String key) { return getBool(key, false); }
    public static boolean getBool (String key, boolean _default) {
        return values.containsKey(key) ? values.get(key).equals("true") : _default;
    }


    private static String configPath = "./toltd.conf";

    public static boolean load () {

        File configFile = new File(configPath);

        if (!configFile.exists() || configFile.isDirectory()) {

            Logging.warn("Failed to locate config file! All values will be generated as default..");
            if (configFile.exists()) configFile.delete();

        } else try {

            for (String line : Files.readAllLines(configFile.toPath())) {

                if (line.isEmpty()) continue;
                if (line.charAt(0) == '#') continue;
                if (!line.contains(":")) continue;

                String[] segments = line.split(":");
                values.put(segments[0], segments[1]);
            }

        } catch (Exception e) {

            Logging.stackWarn(e);
        }

        evaluate();

        return true;
    }

    public static void save () { try {

        File configFile = new File(configPath);
        if (configFile.exists()) {

            for (String key : values.keySet().toArray(new String[values.size()])) {

                boolean valueSet = false;
                for (String line : Files.readAllLines(configFile.toPath())) {

                    if (line.isEmpty()) continue;
                    if (line.charAt(0) == '#') continue;
                    if (!line.contains(":")) continue;

                    if (line.split(":")[0].equals(key)) valueSet = true;
                }

                if (!valueSet) Files.writeString(
                    configFile.toPath(), "\n" + key + ":" + values.get(key),
                    StandardOpenOption.APPEND
                );
            }

        } else {

            configFile.getParentFile().mkdirs();

            String writeCache = "# Config for Tolt Server, generated: ";
            writeCache += String.valueOf(System.currentTimeMillis() / 1000L);
            for (String key : values.keySet().toArray(new String[values.size()]))
                writeCache += "\n" + key + ":" + values.get(key);

            Files.writeString(configFile.toPath(), writeCache);
        }

    } catch (Exception e) { Logging.stackErr(e); } }


    private static String[] defaults = new String[] {

        "server.ipaddress:0.0.0.0",
        "server.port:8282",
        "server.private-key-path:private-key.pem",
        "server.server-cert-path:server-cert.pem",
    };

    private static void evaluate () {

        boolean missingValue = false;

        for (String defaultValue : defaults) {

            String[] segments = defaultValue.split(":");
            if (values.containsKey(segments[0])) continue;

            values.put(segments[0], segments[1]);
            missingValue = true;

            Logging.warn(String.format(
                "Value `%s' was not found! default value: `%s' will be used.",
                segments[0], segments[1]
            ));
        }

        if (missingValue) save();
    }
}
