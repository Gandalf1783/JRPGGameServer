package de.gandalf1783.gameserver.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import de.gandalf1783.gameserver.MySQL.SQLUtils;
import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.entities.Entity;
import de.gandalf1783.gameserver.entities.creatures.Player;
import de.gandalf1783.gameserver.items.Item;
import de.gandalf1783.gameserver.networkhandler.BasicRequestHandler;
import de.gandalf1783.gameserver.objects.*;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;
import org.fusesource.jansi.Ansi;

import java.nio.BufferOverflowException;

import java.util.UUID;

import static de.gandalf1783.gameserver.threads.ConsoleRunnable.println;

public class ServerListener extends Listener {

    @Override
    public void received(Connection connection, Object o) {
        try {
            if(o instanceof BasicRequest) {
                BasicRequest request = (BasicRequest) o;



                BasicRequestHandler.executeResponse(request, connection);

                /*
                * Disconnecting a User from the Server.
                * Connection will be removed from the HashMap and the Server sends a DISCONNECT to all other clients.
                * Also, it prints a Message in the Console
                */
                if(request.text.equalsIgnoreCase("DISCONNECT")) {
                    ClientInteractions.broadcastPlayerDisconnect(Main.getServer(), Main.getUuidHashMap().get(connection).toString());
                    ConsoleRunnable.println("User "+ SQLUtils.receiveName(request.data)+" disconnected.", Ansi.Color.RED);
                    Main.getUuidHashMap().remove(connection);
                }

                /*
                * If a Client asks for all Entities, send them
                *
                * At the end, send the world data.
                */
                if(request.text.equalsIgnoreCase("ENTITIES?")) {

                    //Sending all Players...
                    for(Connection c : Main.getServer().getConnections()) {
                        if(c != connection) {
                            UUID uuid = Main.getUuidHashMap().get(c);
                            Player p = (Player) Main.getWorldInstance().getUuidEntityMap().get(uuid.toString());
                            ClientInteractions.sendAddPlayer(p, connection);
                        }
                    }

                    for(Entity e : Main.getWorldInstance().getUuidEntityMap().values()) {
                        if(e == null) {
                            continue;
                        }

                        if(e instanceof Item) {
                            Item i = (Item) e;
                            ClientInteractions.spawnItemOnGround(i, connection);
                        } else {
                            ClientInteractions.sendAddEntity(e, connection);
                        }

                    }
                    //Sending Players Spawn Position
                    ClientInteractions.sendPlayerSpawnPos(connection);

                    //Sending World Data
                    ClientInteractions.sendSpawnChunks(connection);

                    ClientInteractions.sendReadyToPlay(connection);
                }

                /*
                 * Sends the Statistics to the Player.
                 */
                if(request.text.equalsIgnoreCase("STATS?")) {
                    //Retrieve UUID by connection
                    String uuid = Main.getUuidHashMap().get(connection).toString();

                    // Send the Players Inventory
                    for(Item i : Main.getWorldInstance().getInventoryHashMap().get(uuid).getInventoryItems()) {
                        ClientInteractions.addItemToPlayerInventory(uuid, i, connection);
                    }

                    // Set the Players Health
                    ClientInteractions.setEntityHealth(uuid, Main.getWorldInstance().getUuidEntityMap().get(Main.getUuidHashMap().get(connection).toString()).getHealth()+"", Main.getServer());
                }


                /*
                 * Hits an entity
                 */
                if(request.text.equalsIgnoreCase("HIT")) {
                    try {

                        String[] data = request.data.split("#");
                        String recUUID = Main.getUuidHashMap().get(connection).toString();

                        if(data.length != 2) {
                            incompleteData("HIT", recUUID);
                            return;
                        }

                        String tarUUID = UUID.fromString(data[0]).toString();

                        if (!Main.getWorldInstance().getUuidEntityMap().containsKey(recUUID)) { // If the entity who tried to hit is invalid, return
                            return;
                        }

                        if(!Main.getWorldInstance().getUuidEntityMap().containsKey(tarUUID)) { // Tried to hit an UUID that is not in use! (return)
                            connection.close();
                            return;
                        }


                        int amt = Integer.parseInt(data[1]);
                        int health = Main.getWorldInstance().getUuidEntityMap().get(tarUUID).getHealth();
                        health -= amt;

                        if (health <= 0) {
                            entityDied(UUID.fromString(tarUUID)); // Let the Entity Die
                            return;
                        } else {
                            ClientInteractions.setEntityHealth(tarUUID, health+"", Main.getServer()); // Set the reduced health if any left
                        }

                        if(!Main.getWorldInstance().getUuidEntityMap().containsKey(tarUUID)) // Check should not be needed, but if the Entity is not in the Map anymore, just skip it.
                            return;

                        Main.getWorldInstance().getUuidEntityMap().get(tarUUID).setHealth(health); // Finally, set the correct health

                        println("HIT: ENTITY "+tarUUID+" WAS HIT WITH AMT "+amt+" AND HAS "+health+" HP", Ansi.Color.YELLOW);

                    } catch (NumberFormatException e){

                    }
                }

            } else if(o instanceof Pos) {
                try {
                    UUIDPos p = new UUIDPos();
                    p.uuid = Main.getUuidHashMap().get(connection).toString();
                    p.p = (Pos) o;
                    Pos pos = p.p;

                    if(Main.getUuidHashMap().get(connection) == null) {
                        return;
                    }

                    pos.setDimensionID(Main.getWorldInstance().getUuidEntityMap().get(Main.getUuidHashMap().get(connection).toString()).getPos().getDimensionID());

                    Main.getWorldInstance().getUuidEntityMap().get(p.uuid).setPos(pos);

                    ClientInteractions.broadcastExceptPos(p, connection, Main.getServer().getConnections());
                    Main.getWorldInstance().getUuidEntityMap().get(Main.getUuidHashMap().get(connection).toString()).setPos(pos);
                } catch (NullPointerException e) {
                    println("NullPointerException occurred while setting a Position for an Entity.");
                }
            }
        } catch (BufferOverflowException e) {
            println("[ERROR] - NETWORK BUFFER OVERFLOW!", Ansi.Color.RED);
        }
    }

    @Override
    public void connected(Connection connection) {
    }

    @Override
    public void disconnected(Connection connection) {
        if(Main.getUuidHashMap().get(connection) == null) {
            return;
        }
        String UUID =  Main.getUuidHashMap().get(connection).toString();
        String username = SQLUtils.receiveName(UUID);

        Main.getUuidHashMap().remove(connection);

        Main.currentPlayers = Main.currentPlayers - 1;
        println("> User "+username+" disconnected", Ansi.Color.RED);

        ClientInteractions.broadcastPlayerDisconnect(Main.getServer(), UUID);

    }

    public boolean isUUIDValid(String suuid) {
        try {
            UUID uuid = UUID.fromString(suuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void incompleteData(String command, String recUUID) {
        println("[WARNING] User "+recUUID+" sent an incomplete String for command ["+command+"].", Ansi.Color.YELLOW);
    }

    public void entityDied(UUID uuid) {
        ClientInteractions.broadcastDeadPlayer(uuid, Main.getServer());
        Main.getWorldInstance().getUuidEntityMap().remove(uuid.toString());
        println("Entity "+uuid.toString()+" died!", Ansi.Color.WHITE);
    }

    public static void updatePlayerStats() {
        ClientInteractions.broadcastCurrentPlayers(Main.currentPlayers, Main.getServer());
        ClientInteractions.broadcastMaxPlayers(Main.maxPlayers, Main.getServer());
    }


    private Player createNewPlayer(String uuid, String username) {
        Player p = new Player();
        p.setUuid(uuid);
        p.setHealth(10);
        p.setPos(new Pos());
        p.getPos().setX(0);
        p.getPos().setY(0);
        p.getPos().setDimensionID(0);
        p.setName(username);
        return p;
    }
}
