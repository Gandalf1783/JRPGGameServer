package de.gandalf1783.gameserver.ConsoleCommands;

import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;
import de.gandalf1783.gameserver.world.Generation;
import de.gandalf1783.gameserver.world.World;
import org.fusesource.jansi.Ansi;

public class GenerateAllChunksCommand implements Command {

    private static boolean userWasAsked = false;

    @Override
    public int execute(String[] args) {
        if(userWasAsked == false) {
            userWasAsked = true;
            int chunksAll = Main.getWorldInstance().getWorldChunkSize() * Main.getWorldInstance().getWorldChunkSize();
            ConsoleRunnable.println("We are now going to generate all "+chunksAll+" chunks, aside those which already exist.\n Do you want to do this now?\n Type /generateallchunks <yes/no>", Ansi.Color.RED);
        } else {
            if(args.length == 1) {
                String answer = args[0];

                if(answer.equalsIgnoreCase("yes") && userWasAsked) {
                    userWasAsked = false;
                    ConsoleRunnable.println("Generating all chunks. \nYou will not see the process or any output related to when a chunk has been calculated. \n", Ansi.Color.GREEN);

                    new Thread(()-> {
                        int chunks = Main.getWorldInstance().getWorldChunkSize();
                        int chunkHalf = Main.getWorldInstance().getWorldChunkSize()/2;

                        int[][] noiseMap = World.generateMap(Main.getWorldInstance().getSeed(), Main.getWorldInstance().getWorldChunkSize()*16);

                        long start = System.currentTimeMillis();
                        ConsoleRunnable.println("[ChunkGenerator] Beginning now..");

                        for(int x = 0-chunkHalf; x < chunks-chunkHalf; x++) {
                            for(int y = 0-chunkHalf; y < chunks-chunkHalf; y++) {
                                Main.getWorldInstance().getChunk(x,y, noiseMap);
                            }
                        }

                        long end = System.currentTimeMillis();
                        ConsoleRunnable.println("[ChunkGenerator] Finished loading all Chunks in "+((end-start)/1000)+" seconds!", Ansi.Color.GREEN);
                    }).start();
                    return 0;
                } else if(answer.equalsIgnoreCase("no") && userWasAsked) {
                    userWasAsked = false;
                    ConsoleRunnable.println("Generating all chunks has been cancelled.", Ansi.Color.GREEN);
                    return 0;
                }
            }

            ConsoleRunnable.println("Please type the command again followed by \"yes\" or \"no\": generateallchunks <yes/no>", Ansi.Color.YELLOW);
            return CommandError.SYNTAX_ERR;
        }


        return 0;
    }
}
