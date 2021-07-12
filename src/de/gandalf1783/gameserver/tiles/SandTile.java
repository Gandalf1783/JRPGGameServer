package de.gandalf1783.gameserver.tiles;

public class SandTile extends Tile {

    public SandTile(int id) {
        super(id);
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
