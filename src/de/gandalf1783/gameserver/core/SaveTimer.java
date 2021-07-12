package de.gandalf1783.gameserver.core;

import de.gandalf1783.gameserver.threads.ConsoleRunnable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

public class SaveTimer extends TimerTask {

    @Override
    public void run() {

        ConsoleRunnable.println("\n\n[AUTOSAVE] - "+getDateAsString());
        Main.saveWorldToFile();
        ConsoleRunnable.println("[AUTOSAVE] Complete.\n\n");
    }

    /**
     * @return returns the current date of the host system as a string in DD-MM-YYYY HH:MM:SS format.
     */
    public static String getDateAsString() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return dateFormat.format(date);
    }
}
