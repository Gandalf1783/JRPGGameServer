package de.gandalf1783.gameserver.entities.statics;

import de.gandalf1783.gameserver.entities.Entity;

import java.io.Serializable;

public abstract class StaticEntity extends Entity implements Serializable {

    private Boolean solid = false;

    protected StaticEntity() {

    }

    public Boolean getSolid() {
        return solid;
    }

    public void setSolid(Boolean solid) {
        this.solid = solid;
    }
}
