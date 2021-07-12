package de.gandalf1783.gameserver.tiles;

public class CobbleStoneTile extends Tile {


    public CobbleStoneTile(int id) {
        super(id);
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
