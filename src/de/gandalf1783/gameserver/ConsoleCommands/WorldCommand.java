package de.gandalf1783.gameserver.ConsoleCommands;

import com.esotericsoftware.kryonet.Connection;
import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.entities.Entity;
import de.gandalf1783.gameserver.entities.creatures.Player;
import de.gandalf1783.gameserver.listener.ClientInteractions;
import de.gandalf1783.gameserver.objects.BasicResponse;
import de.gandalf1783.gameserver.objects.Pos;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;
import de.gandalf1783.gameserver.tiles.Tile;
import de.gandalf1783.gameserver.world.Chunk;
import de.gandalf1783.gameserver.world.Generation;
import de.gandalf1783.gameserver.world.World;
import org.fusesource.jansi.Ansi;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class WorldCommand implements Command {


    @Override
    public int execute(String[] args) {
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("createimage")) {

                ConsoleRunnable.println("Creating Image of the Map...", Ansi.Color.YELLOW);

                int size = (Main.getWorldInstance().getWorldChunkSize()*16)+16;

                BufferedImage bufferedImage = new BufferedImage(800,800,
                        BufferedImage.TYPE_INT_RGB);

                for(int x = -25; x < 25; x++) {
                    for(int y = -25; y < 25; y++) {
                        int actualChunkX = x+(Main.getWorldInstance().getWorldChunkSize()/2), actualChunkY = y+(Main.getWorldInstance().getWorldChunkSize()/2);

                            Chunk c = Main.getWorldInstance().getChunks()[actualChunkX][actualChunkY];
                            if(c == null)
                                continue;

                            for(int tileX = 0; tileX < 16; tileX++) {
                                for(int tileZ = 0; tileZ < 16; tileZ++) {

                                    int tile = c.getBlock(tileX, 1 , tileZ);

                                    System.out.println("Draw at "+((actualChunkX*16)+tileX)+ "|"+((actualChunkY*16)+tileZ));

                                    bufferedImage.setRGB((actualChunkX*16)+tileX,(actualChunkY*16)+tileZ, getColorForTile(tile));


                                }
                            }
                    }
                }

                File outputfile = new File("image.jpg");
                try {
                    ImageIO.write(bufferedImage, "jpg", outputfile);
                    ConsoleRunnable.println("Wrote image to image.jpg", Ansi.Color.GREEN);
                } catch (IOException e) {
                    ConsoleRunnable.println("An Error has occured while writing the image: "+e.getMessage(), Ansi.Color.RED);
                    return -1;
                }

                return 0;
            } else if (args[0].equalsIgnoreCase("dim")) {
                Iterator it = Main.getWorldInstance().getUuidEntityMap().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    if (!(pair.getValue() instanceof Player))
                        continue;
                    Player p = (Player) pair.getValue();
                    Pos pos = p.getPos();
                    pos.setDimensionID(1);
                    p.setPos(pos);
                    //TODO: SEND CHUNKS INSTEAD OF THIS!!
                    BasicResponse resp = new BasicResponse();
                    resp.text = "WORLD";
                    resp.data = "";
                    Main.getServer().sendToAllTCP(resp);
                }
                return 0;
            } else if (args[0].equalsIgnoreCase("save")) {
                Main.saveWorldToFile();
                return 0;
            } else if (args[0].equalsIgnoreCase("load")) {
                Main.loadWorld();
                for (Connection c : Main.getServer().getConnections()) {
                    ClientInteractions.sendSpawnChunks(c);
                }
                return 0;
            } else if (args[0].equalsIgnoreCase("size")) {
                ConsoleRunnable.println(Main.getWorldInstance().getWorldChunkSize()+"");
                return 0;
            } else if(args[0].equalsIgnoreCase("generate")) {
                Main.getWorldInstance().getNewMap();

                Iterator it = Main.getWorldInstance().getUuidEntityMap().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if(!(pair.getValue() instanceof Player))
                        continue;
                    Player p = (Player) pair.getValue();
                    Pos pos = new Pos();
                    pos.setDimensionID(0);
                    pos.setX(0);
                    pos.setY(0);
                    p.setPos(pos);
                }
                for(Connection c : Main.getServer().getConnections()) {
                    ClientInteractions.sendSpawnChunks(c);
                }
                return 0;
            } else if(args[0].equalsIgnoreCase("seed")) {
                ConsoleRunnable.println("> Current World Seed is "+Main.getWorldInstance().getSeed());
                return 0;
            } else if(args[0].equalsIgnoreCase("pos")) {
                ConsoleRunnable.println("> Positions:");
                Iterator it = Main.getWorldInstance().getUuidEntityMap().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    if(!(pair.getValue() instanceof Player))
                        continue;
                    Player p = (Player) pair.getValue();
                    Pos pos = p.getPos();
                    ConsoleRunnable.println("> "+pair.getKey() + " |  [" + pos.getX()+"|"+pos.getY()+"] | "+pos.getDimensionID());
                }
                return 0;
            } else if(args[0].equalsIgnoreCase("spawn")) {
                ConsoleRunnable.println("> Default World-Spawn: ["+0+"|"+0+"]");
                return 0;
            } else {
                return CommandError.SUBCOMMAND_DOES_NOT_EXIST;
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("seed")) {
                if(args[1].equalsIgnoreCase("rnd")) {
                    int i = new Random().nextInt(1000000);
                    ConsoleRunnable.println("> New Seed (used for next world-gen): "+i);
                    Generation.SEED = i;
                    return 0;
                } else {
                    try {
                        int i = Integer.parseInt(args[1]);
                        ConsoleRunnable.println("> New Seed (used for next world-gen): "+i);
                       Generation.SEED = i;
                        return 0;
                    } catch (NumberFormatException e) {
                        return CommandError.NUMBER_FORMAT_EXCEPTION;
                    }
                }
            } else if(args[0].equalsIgnoreCase("pos")) {
                if(args[1].equalsIgnoreCase("online") || args[1].equalsIgnoreCase("on")) {
                    ConsoleRunnable.println("> Positions (only online players): ");
                    for(Connection c : Main.getServer().getConnections()) {
                        String uuid = Main.getUuidHashMap().get(c).toString();
                        Pos p = Main.getWorldInstance().getUuidEntityMap().get(uuid).getPos();
                        ConsoleRunnable.println("> "+uuid+" | ["+p.getX()+"|"+p.getY()+"]");
                    }
                    return 0;
                } if(args[1].equalsIgnoreCase("offline")) {
                    ConsoleRunnable.println("> Positions (only offline players): ");
                    Iterator it = Main.getWorldInstance().getUuidEntityMap().entrySet().iterator();
                    ArrayList<String> onlineUUIDs = new ArrayList<>();
                    for(Connection c : Main.getServer().getConnections()) {
                        onlineUUIDs.add(Main.getUuidHashMap().get(c).toString());
                    }
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        if(onlineUUIDs.contains(pair.getKey())) continue;
                        ConsoleRunnable.println("> "+pair.getKey() + " |  [" + ((Entity) pair.getValue()).getPos().getX() +"|"+((Entity) pair.getValue()).getPos().getY()+"] | "+((Entity) pair.getValue()).getPos().getDimensionID());
                    }
                    return 0;
                } else {
                    return CommandError.SUBCOMMAND_DOES_NOT_EXIST;
                }
            } else {
                return CommandError.SUBCOMMAND_DOES_NOT_EXIST;
            }
        } else {
            return CommandError.TOO_MANY_ARGS;
        }
    }

    private static int getColorForTile(int tile) {
        Tile t = Tile.tiles[tile];
        if(t.getC() == null) {
            return new Color(0, 0, 0, 0).getRGB();
        }
        return t.getC().getRGB();
    }
}
