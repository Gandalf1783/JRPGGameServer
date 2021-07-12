package de.gandalf1783.gameserver.threads;

import de.gandalf1783.gameserver.MySQL.SQLUtils;
import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.entities.Entity;
import de.gandalf1783.gameserver.entities.creatures.Player;
import de.gandalf1783.gameserver.items.Item;
import de.gandalf1783.gameserver.listener.ServerListener;
import de.gandalf1783.gameserver.objects.BasicResponse;
import de.gandalf1783.gameserver.objects.EntityQuadTree;
import de.gandalf1783.quadtree.Rectangle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.UUID;


public class EventRunnable implements Runnable {

    private static EntityQuadTree entityQuadTree;

    public static int updatingItems;

    public void init() {


    }

    @Override
    public void run() {
        init();
        long startTime;
        long endTime;
        ArrayList<Entity> entitiesToCheck;

        while (true) {
            startTime = System.currentTimeMillis();

            entityQuadTree = new EntityQuadTree(Main.getWorldInstance().getBoundaries(), 4);

            for(UUID u :Main.getUuidHashMap().values()) {
                if(!Main.getWorldInstance().getUuidEntityMap().containsKey(u.toString())) {
                    continue;
                }
                Entity e = Main.getWorldInstance().getUuidEntityMap().get(u.toString());
                if(e instanceof Player) {
                    Player p = (Player) e;

                    entitiesToCheck = new ArrayList<>();
                    Rectangle area = new Rectangle(p.getPos().getX()-500, p.getPos().getY()-500,1000,1000);

                    try {
                        for(Entity en : Main.getWorldInstance().getUuidEntityMap().values()) {
                            if(en != e) {
                                entityQuadTree.insertEntity(en);
                            }
                        }
                    } catch (ConcurrentModificationException exc) {
                    }

                    entityQuadTree.queryEntities(area, entitiesToCheck);
                    updatingItems = entitiesToCheck.size();
                    interactWithEntitiy(entitiesToCheck, p);
                }
            }

            endTime = System.currentTimeMillis();

            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static void interactWithEntitiy(ArrayList<Entity> entities, Player p) {
        Rectangle playerRect = new Rectangle(p.getPos().getX(), p.getPos().getY(), 64,64);
        for(Entity e : entities) {
            if(e instanceof Item) {
                Item i = (Item) e;
                if(i == null || i.getPos() == null)
                    continue;
                Rectangle itemRect = new Rectangle(i.getPos().getX(), i.getPos().getY(), Main.ITEM_SIZE, Main.ITEM_SIZE);

                if(playerRect.intersects(itemRect) ) {
                    if(i.canBePickedUp()) {
                        InteractNotifier.interactionNotify("ITEM_PICKUP", p.getUuid()+"#"+i.getName()+"#"+i.getCount()+"#"+i.getUuid()+"#"+i.getId());

                        Main.getWorldInstance().getUuidEntityMap().remove(i.getUuid(), i);

                        //Destroy unnecessary values

                        i.setPos(null);
                        i.setUuid(null);

                        Main.getWorldInstance().getInventoryHashMap().get(p.getUuid()).addItem(i);

                    }
                } else {
                    if(!i.canBePickedUp()) {
                        i.setCanBePickedUp(true);
                    }
                }
            }
        }
    }

    public static class InteractNotifier {
        public static void interactionNotify(String interaction, String data) {
            new Thread(() -> {
                BasicResponse resp = new BasicResponse();
                resp.text = interaction;
                resp.data = data;
                Main.getServer().sendToAllTCP(resp);
            }).start();
        }
    }

}
