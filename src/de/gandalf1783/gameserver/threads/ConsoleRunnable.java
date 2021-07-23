package de.gandalf1783.gameserver.threads;

import de.gandalf1783.gameserver.ConsoleCommands.*;
import de.gandalf1783.gameserver.core.Log;
import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.objects.BasicResponse;
import de.gandalf1783.gameserver.objects.Pos;
import org.fusesource.jansi.Ansi;
import org.jline.builtins.Completers;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;


import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.jline.builtins.Completers.TreeCompleter.node;


public class ConsoleRunnable implements Runnable {

    private Terminal terminal;
    private static LineReader lineReader;
    private AggregateCompleter aggregateCompleter;
    private ArgumentCompleter argumentCompleter;
    private HashMap<String, Command> commandList = new HashMap<>();
    private static OutputStreamWriter writer;


    private void init() {
        try {
            writer = new OutputStreamWriter(
                    new FileOutputStream("logfile.log"), "UTF-8");

            writer.write("### Logfile Created at Boot: "+(new Date(System.currentTimeMillis()))+"###\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        commandList.put("world", new WorldCommand());
        commandList.put("stop", new StopCommand());
        commandList.put("connections", new ConnectionsCommand());
        commandList.put("help", new HelpCommand());
        commandList.put("version", new VersionCommand());
        commandList.put("spawn", new SpawnCommand());

        // register tp as alias for teleport
        commandList.put("teleport", new TeleportCommand());
        commandList.put("tp", commandList.get("teleport"));

        commandList.put("generateallchunks", new GenerateAllChunksCommand());



    }

    @Override
    public void run() {
        init();
        try {

            aggregateCompleter = new AggregateCompleter(
                    new Completers.TreeCompleter(
                            node("world",
                                    node("generate",
                                            node("", "<int>")),
                                    node("seed",
                                            node("", "rnd", "<int>")),
                                    node("save"),
                                    node("load"),
                                    node("spawn"),
                                    node("pos",
                                            node("", "online", "offline"))),
                            node("data",
                                    node("delete",
                                            node("<UUID>")),
                                    node("info",
                                            node("delete"))
                            ),
                            node("connections"),
                            node("help"),
                            node("stop"),
                            node("version"),
                            node("generateallchunks"),

                            node("tp",
                                    node("<PlayerName>",
                                            node("x",
                                                    node("y")))
                            )

                    ));

            terminal = TerminalBuilder.builder().system(true).dumb(true).encoding(Charset.forName("UTF-8")).name("Terminal").jna(true).jansi(true).build();
            lineReader = LineReaderBuilder.builder().terminal(this.terminal).completer(aggregateCompleter).build();

        } catch (IOException e) {
            e.printStackTrace();
        }

        String prompt = Ansi.ansi().eraseScreen().fg(Ansi.Color.BLUE).bold().a("SERVER").fgBright(Ansi.Color.BLACK).bold().a(" » ").reset().toString();

        ConsoleRunnable.println("> Console initiated.");

        while (true) {
            try {

                String command = lineReader.readLine(prompt);
                String[] data = command.split("\\s+");

                int commandStatus = 0;
                if(!(data.length >= 1)) continue;

                if(commandList.containsKey(data[0])) {

                    Command c = commandList.get(data[0]);
                    String[] args = new String[data.length-1];

                    for(int i = 1; i < data.length; i++) {
                        args[i-1] = data[i];
                    }

                    commandStatus = c.execute(args);

                } else {

                    commandStatus = CommandError.COMMAND_DOES_NOT_EXIST;

                }

                if(commandStatus == 1)
                    println("Execution Error: No Arguments provided.", Ansi.Color.YELLOW);
                if(commandStatus == 2)
                    println("Execution Error: Missing Arguments.", Ansi.Color.YELLOW);
                if(commandStatus == 3)
                    println("Execution Error: Too many Arguments provided.", Ansi.Color.YELLOW);
                if(commandStatus == 4)
                    println("Execution Error: Syntax Error", Ansi.Color.YELLOW);
                if(commandStatus == 5)
                    println("Execution Error: NumberFormatException. You have to enter a valid number.", Ansi.Color.YELLOW);
                if(commandStatus == 6)
                    println("Execution Error: This subcommand does not exist.", Ansi.Color.YELLOW);
                if(commandStatus == 7)
                    println("Execution Error: This Command does not exist.", Ansi.Color.YELLOW);

            } catch (NumberFormatException e) {

                ConsoleRunnable.println("You did not enter a valid number. Please try again.", Ansi.Color.YELLOW);

            } catch (NullPointerException e) {

                e.printStackTrace();
                ConsoleRunnable.println("Something went wrong and returned NULL. Please try again.", Ansi.Color.RED);

            }
        }
    }

    /**
     * Prints out the String to a Logfile and puts no \n at the end
     * @param s String to print
     */
    public static void printToLog(String s) {
        try {
            if(Log.isLogStarted()) {
                Log.getLogWriter().write(s+"\n");
                Log.getLogWriter().flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void println(String s) {
        if(lineReader == null) {
            System.out.println(s);
            return;
        }
        println(s, Ansi.Color.CYAN);
    }

    public static void println(String s, Ansi.Color c) {
        if(lineReader == null) {
            System.out.println(s);
            return;
        }
        String out = Ansi.ansi().fg(c).bold().a(s).reset().toString();
        lineReader.printAbove(out);
        printToLog(s);
    }
}
