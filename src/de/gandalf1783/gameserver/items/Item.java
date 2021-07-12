package de.gandalf1783.gameserver.items;

import de.gandalf1783.gameserver.entities.Entity;

import java.awt.Rectangle;
import java.io.Serializable;
import java.util.UUID;


public class Item extends Entity implements Serializable {

    private String name;
    private int id;

    private int count;
    private boolean pickedUp = false;

    private boolean canBePickedUp = true;

    public Item(){
        this.uuid = UUID.randomUUID().toString();
        this.count = 1;
        this.name = "New Item";
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPickedUp(boolean pickedUp) {
        this.pickedUp = pickedUp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public boolean isPickedUp() {
        return pickedUp;
    }

    public void setCanBePickedUp(boolean canBePickedUp) {
        this.canBePickedUp = canBePickedUp;
    }

    public boolean canBePickedUp() {
        return canBePickedUp;
    }
}
