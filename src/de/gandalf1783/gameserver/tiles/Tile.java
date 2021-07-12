package de.gandalf1783.gameserver.tiles;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Tile {
	
	//STATIC STUFF HERE
	
	public static Tile[] tiles = new Tile[256];

	public static Tile grassTile = new GrassTile(0);
	public static Tile dirtTile = new DirtTile(1);
	public static Tile rockTile = new RockTile(2);
	public static Tile sandTile = new SandTile(3);
	public static Tile cobbleTile = new CobbleStoneTile(4);
	public static Tile waterTile = new WaterTile(6);
	public static Tile deepWaterTile = new DeepWaterTile(5);

	public static Tile waterfall19 = new Waterfall19(19);
	public static Tile waterfall20 = new Waterfall20(20);
	public static Tile waterfall21 = new Waterfall21(21);

	public static Tile air200 = new Air(200);


	//CLASS
	
	public static final int TILEWIDTH = 53, TILEHEIGHT = 53;

	protected final int id;
	protected boolean isSolid;
	
	public Tile(int id){
		this.isSolid = false;
		this.id = id;
		
		tiles[id] = this;
	}

	public boolean isSolid(){
		return this.isSolid;
	}
	public void setSolid(boolean solid) {
		this.isSolid = solid;
	}
	
	public int getId(){
		return id;
	}
	
}
