
package tolt.server.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.lang.Thread;
import java.lang.StackTraceElement;

public class Logging {

    private static String logDirectory = "./logs/";
    private static DateTimeFormatter logFileDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DateTimeFormatter logDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private static String logPath = null;
    private static File logFile = null;

    public static void start () {

        if (logPath != null) {

            System.out.println("Logging failed to start! Logging has already begun!");
            return;
        }

        int tx = 0;
        do {

            logPath = logDirectory + logFileDateFormat.format(LocalDateTime.now()) + "_" + tx + ".log";
            tx++;

        } while (new File(logPath).exists());

        logFile = new File(logPath);
        logFile.getParentFile().mkdirs();

        log("log started `" + logPath + "'");
    }

    public static synchronized void log (String message) { try {

        if (logFile == null) { throw new Exception("Logging.log() called before Logging.start()!"); }

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

        String post =
            "[" + logDateFormat.format(LocalDateTime.now()) + "]:[" +
            (stacktrace[2].getClassName() + "." + stacktrace[2].getMethodName()) +
            "]: " + message
        ;

        Files.writeString(logFile.toPath(), post + System.lineSeparator(),
            StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        System.out.println(post);

    } catch (Exception e) { e.printStackTrace(); } }
}
