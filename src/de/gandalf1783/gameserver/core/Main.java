package de.gandalf1783.gameserver.core;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import de.gandalf1783.gameserver.MySQL.MySQL;
import de.gandalf1783.gameserver.networkhandler.LoginExecutor;
import de.gandalf1783.gameserver.networkhandler.RequestChunkExecutor;
import de.gandalf1783.gameserver.objects.*;
import de.gandalf1783.gameserver.statics.DBNames;
import de.gandalf1783.gameserver.threads.*;
import de.gandalf1783.gameserver.listener.ServerListener;
import de.gandalf1783.gameserver.world.Chunk;
import de.gandalf1783.gameserver.world.Generation;
import de.gandalf1783.gameserver.world.World;
import de.gandalf1783.quadtree.Rectangle;
import org.fusesource.jansi.Ansi;

import java.io.*;
import java.util.*;

public class Main {

    private static final String VERSION =  "Ver.1.0";
    private static final String NET_VERSION = "NetProt.2.6.3";

    private static String SERVER_LOCATION = "DEV";

    private static Server server;
    private static Kryo kryo;


    private static final ConsoleRunnable consoleRunnable = new ConsoleRunnable();
    private static final StatsRunnable statsRunnable = new StatsRunnable();
    private static final GenerationQueueThread generationQueueThread = new GenerationQueueThread();

    private static final Thread consoleThread = new Thread(consoleRunnable);
    private static final Thread statsThread = new Thread(statsRunnable);
    private static final Thread generationThread = new Thread(generationQueueThread);

    private static OneToOneMap<Connection, UUID> uuidHashMap = new OneToOneMap<>();

    private static World w;

    private static Boolean mapLoaded = false;

    public static int currentPlayers, maxPlayers;

    public static final int TILE_SIZE = 53;
    public static final int ITEM_SIZE = 32;

    public static void main(String[] args) {
        Log.startLog();
        currentPlayers = 0;
        maxPlayers = 15;

        int i = 0;
        for(String s : args) {
            if(s.equalsIgnoreCase("-l")) {
                if(args.length >= 2) {
                    if(args[i+1].equalsIgnoreCase("de")) {
                        SERVER_LOCATION = "DE";
                    } else if(args[i+1].equalsIgnoreCase("dev")) {
                        SERVER_LOCATION = "DEV";
                    } else {
                        ConsoleRunnable.println("Region "+args[i+1]+" is not implemented. Using DEV instead.");
                    }
                }
            }
            i++;
        }
        consoleThread.start();

        ConsoleRunnable.println("[JRPG] Java RPG Sever "+ VERSION +" | Made by Gandalf1783 (c) 2020");
        ConsoleRunnable.println("Starting Server.", Ansi.Color.GREEN);
        ConsoleRunnable.println("[REGION] Region is set to "+SERVER_LOCATION);
        init();

        // Register Networking Commands
        new RequestChunkExecutor("REQUEST_CHUNK");
        new LoginExecutor("LOGIN?");
    }

    /**
     * Initialises the Server.
     *  Loads a world and sets up a Server.
     */
    private static void init() {


        //Testing SQL

        MySQL.initDB(DBNames.retrieveDBFromCode(SERVER_LOCATION));

        w = new World(); //Creating a empty World

        if(!doesWorldFileExist()) {
            if(getWorldInstance() == null) {
                w = new World();
                w.setSeed(Generation.SEED);
            }
        } else {
            loadWorld();
        }

        try {
            server = new Server(65536,32768);
            server.start();
            kryo = server.getKryo();
            
            kryo.register(BasicRequest.class);
            kryo.register(BasicResponse.class);
            kryo.register(Pos.class);
            kryo.register(UUIDPos.class);
            kryo.register(EntityPKG.class);
            kryo.register(Rectangle.class);
            kryo.register(World.class);
            kryo.register(Chunk.class);
            kryo.register(int[][][].class);
            kryo.register(int[][].class);
            kryo.register(int[].class);


            server.bind(54555);
            server.addListener(new ServerListener());
        } catch (IOException e) {
            ConsoleRunnable.println("> Could not bind the Port. Please check if you already have a 2nd Instance of this Server running.", Ansi.Color.RED);
            System.exit(2);
        }


        EventRunnable eventRunnable = new EventRunnable();
        Thread eventThread = new Thread(eventRunnable);
        eventThread.start();

        Timer saveTimer = new Timer();
        saveTimer.schedule(new SaveTimer(), 15000, 300000);

        statsThread.start();
        generationThread.start();

        ConsoleRunnable.println("> Server Ready.");
    }

    /**
     * Returns the current Server Object
     * @return Server (Kryonet)
     */
    public static Server getServer() {
        return server;
    }

    /**
     * Saves the current World to a File at C:User\USERNAME\world.dat or /home/USERNAME/world.dat based on the OS.
     */
    public static void saveWorldToFile() {
        try {
            BasicResponse response = new BasicResponse();
            response.text = "SAVE";
            getServer().sendToAllTCP(response);
            ConsoleRunnable.println("[WORLD] Saving world and crunching some numbers....", Ansi.Color.MAGENTA);
            World w2 = new World();

            if(mapLoaded) {
                w2.setSeed(w.getSeed());
            } else {
                w2.setSeed(Generation.SEED);
            }

            w2.setUuidEntityMap(w.getUuidEntityMap());
            w2.setInventoryHashMap(w.getInventoryHashMap());
            w2.setChunks(w.getChunks());
            w2.setBoundaries(w.getBoundaries());
            w2.setWorldChunkSize(w.getWorldChunkSize());

            String path = System.getProperty("user.home");
            String separator = System.getProperty("file.separator");

            String fullPath = path + separator + "world.obj";

            FileOutputStream fos2 = new FileOutputStream(fullPath);
            ObjectOutputStream oos = new ObjectOutputStream(fos2);

            oos.writeObject(w2);
            oos.close();

            fos2.close();
            ConsoleRunnable.println("[WORLD] World has been saved to "+fullPath, Ansi.Color.MAGENTA);
        } catch (IOException e) {
            ConsoleRunnable.println("[ERROR] [WORLD] World could not be saved. "+e.getMessage(), Ansi.Color.RED);
            e.printStackTrace();
        }
    }

    /**
     * Loads the World from the world.dat file if present
     */
    public static void loadWorld() {
        try {
            String path = System.getProperty("user.home");
            String separator = System.getProperty("file.separator");

            String fullPath = path + separator + "world.obj";


            ConsoleRunnable.println("> Trying to load world from "+fullPath, Ansi.Color.MAGENTA);
            if(Main.getServer() != null) {
                BasicResponse response = new BasicResponse();
                response.text = "LOAD";
               getServer().sendToAllTCP(response);
            }



            FileInputStream fis;
            ObjectInputStream ois;

            fis = new FileInputStream(fullPath);
            ois = new ObjectInputStream(fis);

            World w2;
            w2 = (World) ois.readObject();

            getWorldInstance().setInventoryHashMap(new HashMap<>());
            getWorldInstance().setUuidEntityMap(new HashMap<>());

            // Copy all variables into WorldInstance
            Generation.SEED = w2.getSeed();
            getWorldInstance().setUuidEntityMap(w2.getUuidEntityMap());
            getWorldInstance().setInventoryHashMap(w2.getInventoryHashMap());
            getWorldInstance().setWorldChunkSize(w2.getWorldChunkSize());
            getWorldInstance().setSeed(w2.getSeed());
            getWorldInstance().setBoundaries(w2.getBoundaries());
            getWorldInstance().setChunks(w2.getChunks());

            ois.close();
            fis.close();
            ConsoleRunnable.println("> World loaded." , Ansi.Color.MAGENTA);
            mapLoaded = true;
        } catch (IOException e) {
            ConsoleRunnable.println("> File could not be opened: "+e.getMessage(), Ansi.Color.RED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Tests if World File exists (world.dat)
     * @return Boolean
     */
    private static Boolean doesWorldFileExist() {
        try {

            String path = System.getProperty("user.home");
            String separator = System.getProperty("file.separator");

            String fullPath = path + separator + "world.obj";

            FileInputStream fis;
            fis = new FileInputStream(fullPath);

            ObjectInputStream obis = new ObjectInputStream(fis);
            obis.close();
            fis.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public static OneToOneMap<Connection, UUID> getUuidHashMap() {
        return uuidHashMap;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static World getWorldInstance() {
        return w;
    }
    public static String getNetProtVersion() {
        return NET_VERSION;
    }

}
