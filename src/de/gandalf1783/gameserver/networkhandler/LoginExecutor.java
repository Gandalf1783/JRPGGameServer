package de.gandalf1783.gameserver.networkhandler;

import com.esotericsoftware.kryonet.Connection;
import de.gandalf1783.gameserver.MySQL.SQLUtils;
import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.entities.creatures.Player;
import de.gandalf1783.gameserver.inventory.Inventory;
import de.gandalf1783.gameserver.listener.ClientInteractions;
import de.gandalf1783.gameserver.objects.BasicRequest;
import de.gandalf1783.gameserver.objects.Pos;
import org.fusesource.jansi.Ansi;

import java.util.UUID;

import static de.gandalf1783.gameserver.threads.ConsoleRunnable.println;

public class LoginExecutor extends BasicRequestExecutor{

    public LoginExecutor(String command) {
        super(command);
    }

    @Override
    public void execute(BasicRequest req, Connection conn) {

        UUID uuid;

        if(!isUUIDValid(req.data)) { // If UUID is not valid (format), Terminate the connection.
            println("User with ["+req.data+"] has no valid UUID. Terminating Connection.", Ansi.Color.RED);
            ClientInteractions.ERR_noValidUUID(conn);
            conn.close();
            return;
        }

        uuid = UUID.fromString(req.data);

        if(!SQLUtils.doesUserExist(uuid.toString())) { // Test if UUID / User existst on Server. If not, close Connection
            println("User with ["+req.data+"] does not exist on the SQL. Maybe try a different Server?", Ansi.Color.RED);
            ClientInteractions.ERR_doesNotExists(conn);
            conn.close();
            return;
        }

        //TODO: Test if this works:
        if(Main.getUuidHashMap().containsValue(UUID.fromString(req.data))) { // If a UUID is already online, decline another players connection.
            println("> User disconnected: "+SQLUtils.receiveName(req.data)+" already online!", Ansi.Color.YELLOW);
            ClientInteractions.ERR_uuidAlreadyOnline(conn);
            conn.close();
            return;
        }

        String queriedName =  SQLUtils.receiveName(req.data); // Get Name from SQL

        Main.currentPlayers = Main.currentPlayers + 1; // Update Statistics

        println("> User "+ queriedName +" joined the Server", Ansi.Color.GREEN);


        //Creating a new Player
        Player p = new Player();

        // Send the maximum Amount of Players the server is able to handle
        ClientInteractions.sendMaxPlayers(conn);

        if(Main.getWorldInstance().getUuidEntityMap().containsKey(req.data)) {
            p = (Player) Main.getWorldInstance().getUuidEntityMap().get(req.data);
            p.setName(queriedName); // Name could've change in between games!
            //TODO: Add method for testing if user data is complete. (loaded from world)
        } else {
            Inventory inv = new Inventory();
            p = createNewPlayer(uuid.toString(), queriedName);

            //Craete references to the InventoryHashMap for this Player
            Main.getWorldInstance().getInventoryHashMap().put(req.data, inv);
            //Add the Player to the EntityHashMap
            Main.getWorldInstance().getUuidEntityMap().put(p.getUuid(), p);

            // Done setting up a new Player
        }

        // Assign the UUID with the connection
        Main.getUuidHashMap().put(conn, uuid);

        // Tell everyone who joined
        ClientInteractions.broadcastExceptAddPlayer(p, conn, Main.getServer());

        //sending next indicator, so the client can continue.
        ClientInteractions.sendNextIndicator(conn);
        return;
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

    public boolean isUUIDValid(String suuid) {
        try {
            UUID uuid = UUID.fromString(suuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
