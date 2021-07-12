package de.gandalf1783.gameserver.ConsoleCommands;

import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.objects.BasicResponse;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;
import org.fusesource.jansi.Ansi;

public class StopCommand implements Command {

    @Override
    public int execute(String[] args) {
        ConsoleRunnable.println("Shutdown initiated.", Ansi.Color.YELLOW);
        Main.saveWorldToFile();
        BasicResponse response = new BasicResponse();
        response.text = "SHUTDOWN";
        Main.getServer().sendToAllTCP(response);
        ConsoleRunnable.println("Server will exit now.", Ansi.Color.RED);
        System.exit(0);
        return 0;
    }

}
