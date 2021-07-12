package de.gandalf1783.gameserver.entities;

import de.gandalf1783.gameserver.objects.Pos;

import java.io.Serializable;

public class Entity implements Serializable {

    public static final int DEFAULT_HEALTH = 5;


    protected int EID; // Entity ID
    protected String uuid; // Global UUID
    protected Pos p; // Position of the Entity
    protected int health; // Healt

    // Setting the Basic Variables.
    public Entity() {
        EID = 0;
        uuid = "";
        p = new Pos();
        health = Entity.DEFAULT_HEALTH;
    }

    /**
     * Returns the Entity ID.
     * @return EntityID
     */
    public int getEID() {
        return EID;
    }

    /**
     * Sets the EntityID
     * @param EID EntityID
     */
    public void setEID(int EID) {
        this.EID = EID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Pos getPos() {
        return p;
    }

    public void setPos(Pos p) {
        this.p = p;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
