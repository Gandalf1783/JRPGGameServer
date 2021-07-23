package de.gandalf1783.gameserver.tiles;

import java.awt.*;

public class SandTile extends Tile {

    public SandTile(int id) {
        super(id);
        this.c = Color.YELLOW;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
