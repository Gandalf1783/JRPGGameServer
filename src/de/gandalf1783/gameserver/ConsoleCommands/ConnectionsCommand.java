package de.gandalf1783.gameserver.ConsoleCommands;

import com.esotericsoftware.kryonet.Connection;
import de.gandalf1783.gameserver.MySQL.SQLUtils;
import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;
import org.fusesource.jansi.Ansi;

public class ConnectionsCommand implements Command{

    @Override
    public int execute(String[] args) {
        ConsoleRunnable.println("> Active Connections to the Server:");
        ConsoleRunnable.println("> (Querying Database, may take a while)");
        ConsoleRunnable.println("> "+ Main.getServer().getConnections().length);
        for(Connection c : Main.getServer().getConnections()) {
            String UUID = Main.getUuidHashMap().get(c).toString();
            String IP = c.getRemoteAddressTCP().toString().split(":")[0].replace("/", "");
            String PING = c.getReturnTripTime()+"";
            String NAME = SQLUtils.receiveName(UUID);
            ConsoleRunnable.println("["+NAME+" @ "+IP+" | "+PING+" ms] - "+UUID);
        }
        return 0;
    }

}
