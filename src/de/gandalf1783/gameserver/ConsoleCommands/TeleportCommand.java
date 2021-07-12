package de.gandalf1783.gameserver.ConsoleCommands;

import com.esotericsoftware.kryonet.Connection;
import de.gandalf1783.gameserver.MySQL.SQLUtils;
import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.listener.ClientInteractions;
import de.gandalf1783.gameserver.objects.Pos;

import java.util.UUID;

public class TeleportCommand implements Command {


    @Override
    public int execute(String[] args) {
        if(args.length == 3) {
            String username = args[0];
            String uuid = SQLUtils.receiveUUID(username);

            int x = Integer.parseInt(args[1]);
            int y = Integer.parseInt(args[2]);

            Pos p = new Pos();
            p.setX(x);
            p.setY(y);

            Connection conn = Main.getUuidHashMap().getKey(UUID.fromString(uuid));

            ClientInteractions.teleport(conn, p);

        }
        return 0;
    }


}
