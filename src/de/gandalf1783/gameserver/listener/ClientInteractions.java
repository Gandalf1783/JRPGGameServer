package de.gandalf1783.gameserver.listener;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.entities.Entity;
import de.gandalf1783.gameserver.entities.creatures.Player;
import de.gandalf1783.gameserver.items.Item;
import de.gandalf1783.gameserver.objects.BasicResponse;
import de.gandalf1783.gameserver.objects.Pos;
import de.gandalf1783.gameserver.objects.UUIDPos;
import de.gandalf1783.gameserver.world.Chunk;
import de.gandalf1783.gameserver.world.World;

import java.util.UUID;


public class ClientInteractions {


    public static void broadcastDeadPlayer(UUID uuid, Server server) {
        BasicResponse resp = new BasicResponse();
        resp.text = "DIE";
        resp.data = uuid.toString();
        broadcastObject(resp, server);
    }
    public static void broadcastExceptPos(UUIDPos p, Connection conn, Connection[] connections) {
        broadcastExceptObject(p, conn, connections);
    }

    public static void setEntityHealth(String uuid, String health, Server server) {
        BasicResponse resp = new BasicResponse();
        resp.text = "HEALTH";
        resp.data = uuid+"#"+ health;
        broadcastObject(resp, server);
    }

    public static void addItemToPlayerInventory(String uuid, Item i, Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "ITEM_PICKUP";
        resp.data = uuid +"#"+i.getName()+"#"+i.getCount()+"#"+i.getUuid()+"#"+i.getId();
        sendObject(resp, conn);
    }

    public static void sendPlayerSpawnPos(Connection conn) {
        UUIDPos uPos = new UUIDPos();
        uPos.uuid = Main.getUuidHashMap().get(conn).toString();
        Entity e  = Main.getWorldInstance().getUuidEntityMap().get(uPos.uuid);
        uPos.p = e.getPos();
        uPos.direction = 0;

        conn.sendTCP(uPos);
    }

    public static void spawnItemOnGround(Item i, Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "ITEM_GROUND";
        resp.data = i.getUuid()+"#"+i.getPos().getX()+"#"+i.getPos().getY()+"#"+i.getName()+"#"+i.getCount()+"#"+i.getId();
        sendObject(resp, conn);
    }

    public static void sendNextIndicator(Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "NEXT";
        resp.data = "1";
        sendObject(resp, conn);
    }

    public static void sendAddEntity(Entity e, Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "ENTITYADD";
        resp.data = e.getPos().getX()+"#"+e.getPos().getY()+"#"+e.getHealth()+"#"+e.getUuid()+"#"+e.getPos().getDimensionID()+"#"+e.getEID();
        conn.sendTCP(resp);
    }

    public static void sendAddPlayer(Player p, Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "PLAYERADD";
        resp.data = p.getPos().getX()+"#"+p.getPos().getY()+"#"+p.getHealth()+"#"+p.getUuid()+"#"+p.getPos().getDimensionID()+"#"+p.getName();
        sendObject(resp, conn);
    }
    public static void broadcastExceptAddPlayer(Player p, Connection conn, Server server) {
        BasicResponse resp = new BasicResponse();
        resp.text = "PLAYERADD";
        resp.data = p.getPos().getX()+"#"+p.getPos().getY()+"#"+p.getHealth()+"#"+p.getUuid()+"#"+p.getPos().getDimensionID()+"#"+p.getName();
        broadcastExceptObject(resp, conn, server.getConnections());
    }

    public static void sendMaxPlayers(Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "MAX_PLAYERS";
        resp.data = "15";
        sendObject(resp, conn);
    }
    public static void broadcastCurrentPlayers(int currentPlayers,Server server) {
        BasicResponse resp = new BasicResponse();
        resp.text = "CURRENT_PLAYERS";
        resp.data = currentPlayers+"";
        broadcastObject(resp, server);
    }
    public static void broadcastMaxPlayers(int maxPlayers, Server server) {
        BasicResponse resp = new BasicResponse();
        resp.text = "MAX_PLAYERS";
        resp.data = maxPlayers+"";
        broadcastObject(resp, server);
    }
    public static void broadcastPlayerDisconnect(Server server, String UUID) {
        BasicResponse resp = new BasicResponse();
        resp.text = "DISCONNECT";
        resp.data = UUID;
        broadcastObject(resp, server);
    }
    public static void sendReadyToPlay(Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "RTP";
        resp.data = "";
        sendObject(resp, conn);
    }

    public static void teleport(Connection conn, Pos p) {
        UUID uuid = Main.getUuidHashMap().get(conn);

        BasicResponse resp = new BasicResponse();
        resp.text = "POS_SEND_PAUSE";
        resp.data = "";

        sendObject(resp, conn);

        UUIDPos uuidPos = new UUIDPos();
        uuidPos.uuid = uuid+"";
        uuidPos.p = p;

        broadcastObject(uuidPos, Main.getServer());

        resp.text = "POS_SEND_CONTINUE";
        sendObject(resp, conn);
    }


    /*



    ERROR MESSAGES HERE


    */

    public static void ERR_uuidAlreadyOnline(Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "UUID";
        resp.data = "IN_USE";
        sendObject(resp, conn);
    }

    public static void ERR_noValidUUID(Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "UUID";
        resp.data = "INVALID";
        sendObject(resp, conn);
    }
    public static void ERR_doesNotExists(Connection conn) {
        BasicResponse resp = new BasicResponse();
        resp.text = "UUID";
        resp.data = "NOT_EXISTENT";
        sendObject(resp, conn);
    }


    /*
    Sending Methods:

    */
    private static void broadcastExceptObject(Object o, Connection conn, Connection[] connections) {
        for(Connection c : connections) {
            if(c != conn)
                c.sendTCP(o);
        }
    }
    private static void broadcastObject(Object o, Server server) {
        server.sendToAllTCP(o);
    }
    private static void sendObject(Object o, Connection conn) {
        conn.sendTCP(o);
    }
    public static void sendSpawnChunks(Connection conn) {
        World w = Main.getWorldInstance();
        Chunk[] chunks = w.getSpawnChunks();
        for(Chunk c : chunks) {
            if(c == null)
                continue;
            conn.sendTCP(c);
        }
    }
    public static void sendChunk(int chunkX, int chunkY, Connection conn) {
        //TODO: Change World to Main.getWorldInstance()
        World w = Main.getWorldInstance();
        Chunk c = w.getChunk(chunkX, chunkY);
        if(c != null)  {
            conn.sendTCP(c);
        }
    }
}
