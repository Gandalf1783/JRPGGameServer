package de.gandalf1783.gameserver.entities.creatures;

import java.io.Serializable;

public class Player extends Creature implements Serializable {

    private String name;

    public Player() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
