package de.gandalf1783.gameserver.threads;

import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.listener.ServerListener;
import org.fusesource.jansi.Ansi;

public class StatsRunnable implements Runnable {

    long delta = 40000;
    long lastTime = System.currentTimeMillis()-delta; // Force update on first run

    @Override
    public void run() {
        ConsoleRunnable.println("Statistics Thread started.");
        while (true) {
            if(System.currentTimeMillis()-lastTime > delta) {
                ServerListener.updatePlayerStats();
                //ConsoleRunnable.println("Pushed Statistics.", Ansi.Color.CYAN);
                lastTime = System.currentTimeMillis();
            }
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
