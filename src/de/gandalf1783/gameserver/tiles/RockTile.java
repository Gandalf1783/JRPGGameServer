package de.gandalf1783.gameserver.tiles;

public class RockTile extends Tile {

	public RockTile(int id) {
		super(id);
	}
	
	@Override
	public boolean isSolid(){
		return true;
	}

}
