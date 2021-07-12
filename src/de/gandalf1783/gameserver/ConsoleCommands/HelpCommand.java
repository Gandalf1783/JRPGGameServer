package de.gandalf1783.gameserver.ConsoleCommands;

import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;

import java.io.Console;

public class HelpCommand implements Command {
    @Override
    public int execute(String[] args) {
        ConsoleRunnable.println("");
        ConsoleRunnable.println("*** Java RPG Sever "+ Main.getVersion()+" | Made by Gandalf1783 (c) 2020 ***");
        ConsoleRunnable.println("> Help");
        ConsoleRunnable.println("> stop - Stops the Server and disconnects every client");
        ConsoleRunnable.println("> connections - Shows the Active Connections to the Server");
        ConsoleRunnable.println("> world - All about the World!");
        ConsoleRunnable.println("> version - Prints the version");
        ConsoleRunnable.println("> connections - Lists all Connections");
        ConsoleRunnable.println("");
        return 0;
    }
}
