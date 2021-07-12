package de.gandalf1783.gameserver.ConsoleCommands;

import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;

public class VersionCommand implements Command {
    @Override
    public int execute(String[] args) {
        ConsoleRunnable.println("> This Server is running "+ Main.getVersion()+" on NetProtoc "+Main.getNetProtVersion());
        ConsoleRunnable.println("> Notice: Clients which versions do not match with this may be able to join and play just fine.\nHowever, we cant guarantee that the server runs without problems.");
        return 0;
    }
}
