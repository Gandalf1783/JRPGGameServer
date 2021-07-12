
package de.gandalf1783.gameserver.core;

import de.gandalf1783.gameserver.threads.ConsoleRunnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class Log {

    private static File logFile;
    private static FileWriter logWriter;
    private static boolean logStarted = false;

    /**
     * Creates a LOG File and enables OUTPUT to this file.
     */
    public static void startLog() {
        long bootUpTime = System.currentTimeMillis();
        String name = "output.log";
        name = name.replace("%DATE%", ""+new Date(bootUpTime).toString())
                .replace("\\s+", "_")
                .replace(" ", "_")
                .replace(":", "_");
        logFile = new File(name);
        try {
            logFile.createNewFile();
            if(!logFile.canRead() || !logFile.canWrite()) {
                ConsoleRunnable.println("[MAIN] (startLog) : ERROR : PERMISSIONS NOT SET CORRECTLY");
                System.exit(1);
            }
            logWriter = new FileWriter(name);
            logWriter.write("[MAIN] (startLog): CREATED FOR BOOT AT "+ new Date(bootUpTime)+"\n");
            logWriter.flush();
            logStarted = true;
        } catch (IOException e) {
            ConsoleRunnable.println("[MAIN] (startLog) COULD NOT CREATE FILE \""+name+"\"");
        }
    }

    public static boolean isLogStarted() {
        return logStarted;
    }

    public static File getLogFile() {
        return logFile;
    }

    public static FileWriter getLogWriter() {
        return logWriter;
    }
}