package de.gandalf1783.gameserver.ConsoleCommands;

import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.world.Chunk;
import de.gandalf1783.gameserver.world.World;

import java.awt.image.BufferedImage;

public class CreateImageCommand implements Command {


    @Override
    public int execute(String[] args) {
        World w = Main.getWorldInstance();
        Chunk[][] chunks = w.getChunks();

        BufferedImage img = new BufferedImage(1024, 1024, BufferedImage.TYPE_INT_RGB);

        for(int x1 = 0; x1 < w.getWorldChunkSize(); x1++) {
            for(int y1 = 0; y1 < w.getWorldChunkSize(); y1++) {
                Chunk c = chunks[x1][y1];
                if(c != null) {
                    int[][][] blocks = c.getBlocks();

                    for (int y2 = 0; y2 < 4; y2++) {
                        for (int x2 = 0; x2 < 16; x2++) {
                            for (int z2 = 0; z2 < 16; z2++) {

                                int block = blocks[x2][y2][z2];




                            }
                        }
                    }


                }
            }
        }

        return 0;
    }


}
