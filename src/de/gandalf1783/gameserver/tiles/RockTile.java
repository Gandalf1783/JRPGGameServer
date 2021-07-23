package de.gandalf1783.gameserver.tiles;

import java.awt.*;

public class RockTile extends Tile {

	public RockTile(int id) {
		super(id);
		this.c = Color.DARK_GRAY;
	}
	
	@Override
	public boolean isSolid(){
		return true;
	}

}
