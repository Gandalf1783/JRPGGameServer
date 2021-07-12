package de.gandalf1783.gameserver.objects;

import java.io.Serializable;

public class Pos implements Serializable {

    /**
     *
     */
    private final long serialVersionUID = -3551438266902369304L;

    private float x;
    private float y;
    private int direction;
    private int dimensionID;

    public Pos() {

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getDimensionID() {
        return dimensionID;
    }

    public void setDimensionID(int dimensionID) {
        this.dimensionID = dimensionID;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
