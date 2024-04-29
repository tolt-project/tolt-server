
package tolt.server.service.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.lang.Thread;
import java.lang.StackTraceElement;

import tolt.server.service.logging.ConsoleColors;

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

    public static void success (String message) {

        printLog(ConsoleColors.ANSI_GREEN, message);
    }
    public static void log (String message) {

        printLog(message);
    }
    public static void warn (String warning) {

        printLog(ConsoleColors.ANSI_YELLOW, warning);
    }
    public static void err (String error) {

        printLog(ConsoleColors.ANSI_RED, error);
    }
    public static void crit (String error) {

        printLog(ConsoleColors.ANSI_RED_BACKGROUND + ConsoleColors.ANSI_WHITE, error);
    }
    public static void debug (String error) {

        printLog(ConsoleColors.ANSI_PURPLE, error);
    }

    public static void stackWarn (Exception e) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTraceMessage = stringWriter.toString();

        printLog(ConsoleColors.ANSI_YELLOW, stackTraceMessage, false);
    }
    public static void stackErr (Exception e) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTraceMessage = stringWriter.toString();

        printLog(ConsoleColors.ANSI_RED, stackTraceMessage, false);
    }
    public static void stackCrit (Exception e) {

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String stackTraceMessage = stringWriter.toString();

        printLog(
            ConsoleColors.ANSI_RED_BACKGROUND + ConsoleColors.ANSI_WHITE, stackTraceMessage, false
        );
    }

    private static void printLog(String message) {

        _printLog("", message, true);
    }
    private static void printLog(String colorCode, String message) {

        _printLog(colorCode, message, true);
    }
    private static void printLog(String colorCode, String message, boolean showTime) {

        _printLog(colorCode, message, showTime);
    }
    private static synchronized void _printLog (String colorCode, String message, boolean showTime) { try {

        if (logFile == null) { throw new Exception("Logging.log() called before Logging.start()!"); }

        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();

        String post = !showTime ? message :
            "[" + logDateFormat.format(LocalDateTime.now()) + "]:[" +
            (stacktrace[4].getClassName() + "." + stacktrace[4].getMethodName()) +
            "]: " + message
        ;

        Files.writeString(logFile.toPath(), post + System.lineSeparator(),
            StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        System.out.println(colorCode + post + ConsoleColors.ANSI_RESET);

    } catch (Exception e) { e.printStackTrace(); } }
}
