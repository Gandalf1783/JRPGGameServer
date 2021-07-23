package de.gandalf1783.gameserver.tiles;

import java.awt.*;

public class CobbleStoneTile extends Tile {


    public CobbleStoneTile(int id) {
        super(id);
        this.c = Color.LIGHT_GRAY;
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
