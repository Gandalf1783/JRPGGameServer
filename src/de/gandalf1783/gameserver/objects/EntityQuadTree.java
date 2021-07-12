package de.gandalf1783.gameserver.objects;

import de.gandalf1783.gameserver.entities.Entity;
import de.gandalf1783.quadtree.Point;
import de.gandalf1783.quadtree.QuadTree;
import de.gandalf1783.quadtree.Rectangle;

import java.util.ArrayList;

public class EntityQuadTree extends QuadTree {

    private ArrayList<Entity> entities = new ArrayList<>();

    public EntityQuadTree northwest;
    public EntityQuadTree northeast;
    public EntityQuadTree southwest;
    public EntityQuadTree southeast;

    public EntityQuadTree(Rectangle boundary, int capacity) {
        super(boundary, capacity);

    }

    public void insertEntity(Entity e) {

        Point entityPos = new Point(e.getPos().getX(), e.getPos().getY());

        if (this.boundary.contains(entityPos)) {
            if (this.entities.size() < this.capacity) {
                this.entities.add(e);
            } else {
                if (!this.divided) {
                    this.subdivide();
                }
                this.northwest.insertEntity(e);
                this.northeast.insertEntity(e);
                this.southeast.insertEntity(e);
                this.southwest.insertEntity(e);
            }

        }
    }

    public void subdivide() {
        float x = this.boundary.x;
        float y = this.boundary.y;
        float w = this.boundary.w;
        float h = this.boundary.h;
        Rectangle nw = new Rectangle(x, y, w / 2.0F, h / 2.0F);
        Rectangle ne = new Rectangle(x + w / 2.0F, y, w / 2.0F, h / 2.0F);
        Rectangle se = new Rectangle(x + w / 2.0F, y + h / 2.0F, w / 2.0F, h / 2.0F);
        Rectangle sw = new Rectangle(x, y + h / 2.0F, w / 2.0F, h / 2.0F);
        this.northwest = new EntityQuadTree(nw, this.capacity);
        this.northeast = new EntityQuadTree(ne, this.capacity);
        this.southwest = new EntityQuadTree(sw, this.capacity);
        this.southeast = new EntityQuadTree(se, this.capacity);
        this.divided = true;
    }

    public void queryEntities(Rectangle area, ArrayList<Entity> found) {
        if (!this.boundary.intersects(area)) {
            return;
        } else {
            for(Entity e : this.entities) {
                if(e.getPos() == null)
                    continue;
                if(!area.contains(new Point(e.getPos().getX(), e.getPos().getY()))) {
                    continue;
                }
                found.add(e);
            }
            if(this.divided) {
                this.northwest.queryEntities(area, found);
                this.northeast.queryEntities(area, found);
                this.southwest.queryEntities(area, found);
                this.southeast.queryEntities(area, found);
            }
        }
    }

}
