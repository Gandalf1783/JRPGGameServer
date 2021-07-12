package de.gandalf1783.gameserver.threads;

import com.esotericsoftware.kryonet.Connection;
import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.listener.ClientInteractions;
import de.gandalf1783.gameserver.objects.Pos;
import de.gandalf1783.gameserver.world.Chunk;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

public class GenerationQueueThread implements Runnable {


    public static ArrayList<String> chunkRequested = new ArrayList<>();
    public static HashMap<String, ArrayList<Connection>> requests = new HashMap<>();


    private static void init() {
        ConsoleRunnable.println("Initiated GenerationQueue");
    }

    @Override
    public void run() {
        init();
        while (true) {
            try {

                if(chunkRequested.size() != 0) {
                    for(String s: chunkRequested) {
                        if(s == null) {
                            chunkRequested.remove(s);
                            requests.remove(s);
                            continue;
                        }

                        String[] data = s.split("#");
                        if(data.length != 2)
                            continue;

                        int chunkX = Integer.parseInt(data[0]);
                        int chunkY = Integer.parseInt(data[1]);

                        int indexOffset = Main.getWorldInstance().getWorldChunkSize()/2 ;

                        ConsoleRunnable.println("Chunk has been requested ("+chunkX+"|"+chunkY+")", Ansi.Color.YELLOW);

                        if(!Main.getWorldInstance().isChunkGenerated(chunkX, chunkY)) {
                            ConsoleRunnable.println("Generating Chunk at "+chunkX+" | "+chunkY+"\n", Ansi.Color.YELLOW);
                            ConsoleRunnable.printToLog("Generating Chunk at "+chunkX+" | "+chunkY+"\n");
                            long start = System.currentTimeMillis();

                            Chunk c = Main.getWorldInstance().getChunk(chunkX, chunkY);

                            Main.getWorldInstance().setChunk(chunkX+indexOffset, chunkY+indexOffset, c);

                            long end = System.currentTimeMillis();

                            ConsoleRunnable.printToLog("Generated Chunk "+chunkX+ " | "+chunkY+". Took "+(end-start)+"ms to complete!\n");
                            ConsoleRunnable.println("Generated Chunk "+chunkX+ " | "+chunkY+". Took "+(end-start)+"ms to complete!\n", Ansi.Color.YELLOW);
                            for(Connection conn : requests.get(s)) {
                                ClientInteractions.sendChunk(chunkX, chunkY, conn);
                            }
                        }

                        removeRequest(s);
                    }
                } else {
                    Thread.sleep(100);
                }

            } catch (ConcurrentModificationException e) {
            } catch (InterruptedException e) {
            }
        }

    }

    private static void removeRequest(String s) {
        if(chunkRequested.contains(s)) {
            chunkRequested.remove(s);
        }
        if(requests.containsKey(s)) {
            requests.remove(s);
        }
    }

    public static void addRequest(String s, Connection conn) {
        if(chunkRequested.contains(s) && requests.containsKey(s)) {

            ArrayList<Connection> conns = requests.get(s);
            if(!conns.contains(conn)) {
                conns.add(conn);
                requests.put(s, conns);
            }
        } else {

            chunkRequested.add(s);
            ArrayList<Connection> conns = new ArrayList<>();
            conns.add(conn);
            requests.put(s, conns);
        }
    }

}
