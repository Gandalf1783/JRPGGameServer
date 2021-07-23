package de.gandalf1783.gameserver.world;

import de.gandalf1783.gameserver.entities.Entity;
import de.gandalf1783.gameserver.inventory.Inventory;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;
import de.gandalf1783.gameserver.tiles.Tile;
import org.fusesource.jansi.Ansi;
import de.gandalf1783.quadtree.Rectangle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class World implements Serializable {

    private int worldChunkSize = 50;

    // Chunks are saved in x/y dimensions, no height
    // We have 50 Chunks in each direction
    // 25/25 is middle chunk then
    //            x y
    private Chunk[][] chunks = new Chunk[worldChunkSize+1][worldChunkSize+1];

    private HashMap<String, Entity> uuidEntityMap = new HashMap<>();
    private HashMap<String, Inventory> inventoryHashMap = new HashMap<>();
    private final int spawnChunkX = 0, spawnChunkY = 0;

    private long seed;

    private Rectangle boundaries;

    public World() {
        boundaries = new Rectangle(worldChunkSize*16/2,worldChunkSize*16/2,worldChunkSize*16,worldChunkSize*16);
    }





    public static int[][] generateMap(long seed,int iterations) {
        Generation.SEED = seed;
        Generation.OCEAN_SEED = seed+1;

        double[][] landNoiseMap = Generation.generateNoiseMap(130,iterations);
        double[][] oceanNoiseMap = Generation.generateNoiseMap(120,iterations);

        int[][] landTileMap = Generation.convertValuesToLandWaterMap(landNoiseMap); // Step 1
        int[][] oceanOverlayMap = Generation.convertValuesToLandOceanMap(oceanNoiseMap); // Step 2

        int[][] finalArray = Generation.overlayAllSteps(landTileMap, oceanOverlayMap);

        return finalArray;
    }


    public void getNewMap() {
        ConsoleRunnable.println("Creating a new Map... ", Ansi.Color.RED);
        long start = System.currentTimeMillis();

        seed = 564981656646512130L; // TODO: Temporary SEED!

        int[][] finalArray = generateMap(this.seed, this.worldChunkSize*16);

        long end = System.currentTimeMillis();
        ConsoleRunnable.println("Took "+(end-start)+" ms to generate!");
    }

    public Chunk getChunk(int x, int y, int[][] tileMapFromNoiseGenerator) {

        int actualChunkX = x+(worldChunkSize/2), actualChunkY = y+(worldChunkSize/2);

        if(!((actualChunkX >= 0 ) && (actualChunkX < worldChunkSize) && (actualChunkY >= 0) && (actualChunkY < worldChunkSize))) {
            return null;
        }

        if(chunks[actualChunkX][actualChunkY] != null) { // If the Chunk exists, return it. Otherwise, generate a new Chunk!
            return chunks[actualChunkX][actualChunkY];
        } else {
            return generateChunk(x, y, tileMapFromNoiseGenerator);
        }
    }

    public Chunk getChunk(int x, int y) {

        int actualChunkX = x+(worldChunkSize/2), actualChunkY = y+(worldChunkSize/2);

        if(!((actualChunkX >= 0 ) && (actualChunkX < worldChunkSize) && (actualChunkY >= 0) && (actualChunkY < worldChunkSize))) {
            return null;
        }

        if(chunks[actualChunkX][actualChunkY] != null) { // If the Chunk exists, return it. Otherwise, generate a new Chunk!
            return chunks[actualChunkX][actualChunkY];
        } else {
            return generateChunk(x, y);
        }
    }

    public Chunk generateChunk(int x, int y, int[][] tileMapFromNoiseGenerator) {
        // TODO: Generate Chunk and return then!
        // TODO: ATTENTION: DO THIS IN A NEW THREAD!!!!
        int actualChunkX = x+(worldChunkSize/2), actualChunkY = y+(worldChunkSize/2);

        Chunk c = new Chunk();

        c.setChunkX(x);
        c.setChunkY(y);

        byte chunkTileX = 0, chunkTileZ = 0; // Only counts to 16, nothing more

        for(int tileX = (actualChunkX)*16; tileX < (1+actualChunkX)*16; tileX++) {
            chunkTileX = 0;
            for(int tileY = (actualChunkY)*16; tileY < (1+actualChunkY)*16; tileY++) {
                
                c.setBlock(chunkTileX, 0, chunkTileZ, 2); // Set Underlaying Structure as Rock
                c.setBlock(chunkTileX, 1, chunkTileZ, tileMapFromNoiseGenerator[tileX][tileY]); // The one the player sees if isnt air!

                chunkTileX++;
            }

            chunkTileZ++;
        }

        chunks[actualChunkX][actualChunkY] = c; // Saving the chunk as regular Chunk of World here, so it can be loaded later on

        return c;
    }

    public Chunk generateChunk(int x, int y) {
        // TODO: Generate Chunk and return then!
        // TODO: ATTENTION: DO THIS IN A NEW THREAD!!!!
        int actualChunkX = x+(worldChunkSize/2), actualChunkY = y+(worldChunkSize/2);


        int[][] tileMapFromNoiseGenerator = generateMap(this.seed, this.worldChunkSize*16);

        Chunk c = new Chunk();

        c.setChunkX(x);
        c.setChunkY(y);

        for(byte tileInChunkZ = 0; tileInChunkZ < 16; tileInChunkZ++) {
            for(byte tileInChunkX = 0; tileInChunkX < 16; tileInChunkX++) {

                int tile = tileMapFromNoiseGenerator[(actualChunkX*16) + tileInChunkX][(actualChunkY*16) + tileInChunkZ];

                c.setBlock(tileInChunkX, 0, tileInChunkZ, 2);
                c.setBlock(tileInChunkX, 1, tileInChunkZ, tile);
            }
        }

        chunks[actualChunkX][actualChunkY] = c; // Saving the chunk as regular Chunk of World here, so it can be loaded later on

        return c;
    }

    public Chunk[] getSpawnChunks() {
        int radius = 3;
        int i = 0;

        int xIndexOffset = worldChunkSize/2;
        int yIndexOffset = worldChunkSize/2;

        Chunk[] spawnchunks = new Chunk[radius*radius*4];

        for(int x = spawnChunkX-radius; x < spawnChunkX+radius; x++) {
            for(int y = spawnChunkY-radius; y < spawnChunkY+radius; y++) {
                spawnchunks[i] = getChunk(x,y);
                i++;
            }
        }

        return spawnchunks;
    }

    public boolean isChunkGenerated(int chunkX, int chunkY) {

        int indexOffset = worldChunkSize/2;
        if(chunkX+indexOffset < worldChunkSize || chunkY+indexOffset < worldChunkSize || chunkX+indexOffset > worldChunkSize || chunkY+indexOffset > worldChunkSize) {
            return true;
        }
        return (this.chunks[chunkX+indexOffset][chunkY+indexOffset] != null);
    }

    public int getWorldChunkSize() {
        return worldChunkSize;
    }

    public void setWorldChunkSize(int worldChunkSize) {
        this.worldChunkSize = worldChunkSize;
    }

    public Chunk[][] getChunks() {
        return chunks;
    }

    public void setChunks(Chunk[][] chunks) {
        this.chunks = chunks;
    }

    public HashMap<String, Entity> getUuidEntityMap() {
        return uuidEntityMap;
    }

    public void setUuidEntityMap(HashMap<String, Entity> uuidEntityMap) {
        this.uuidEntityMap = uuidEntityMap;
    }

    public HashMap<String, Inventory> getInventoryHashMap() {
        return inventoryHashMap;
    }

    public void setInventoryHashMap(HashMap<String, Inventory> inventoryHashMap) {
        this.inventoryHashMap = inventoryHashMap;
    }

    public int getSpawnChunkX() {
        return spawnChunkX;
    }

    public int getSpawnChunkY() {
        return spawnChunkY;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public Rectangle getBoundaries() {
        return boundaries;
    }

    public void setBoundaries(Rectangle boundaries) {
        this.boundaries = boundaries;
    }

    public void setChunk(int x, int y, Chunk c) {
        this.chunks[x][y] = c;
    }
}
