package de.gandalf1783.gameserver.ConsoleCommands;

import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.entities.statics.Cactus;
import de.gandalf1783.gameserver.entities.statics.Rock;
import de.gandalf1783.gameserver.objects.BasicResponse;
import de.gandalf1783.gameserver.objects.Pos;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;

import java.util.UUID;

public class SpawnCommand implements Command {

    @Override
    public int execute(String[] args) {
        Cactus c = new Cactus();
        c.setUuid(UUID.randomUUID().toString());
        Pos pos = new Pos();
        pos.setY(150);
        pos.setY(150);
        c.setPos(pos);
        c.setHealth(10);
        Main.getWorldInstance().getUuidEntityMap().put(c.getUuid().toString(), c);

        Rock r = new Rock();
        r.setUuid(UUID.randomUUID().toString());
        pos.setX(220);
        pos.setY(220);
        r.setPos(pos);
        r.setHealth(10);
        Main.getWorldInstance().getUuidEntityMap().put(r.getUuid().toString(), r);
        BasicResponse resp = new BasicResponse();
        resp.text = "ENTITIES_UPDATE";
        Main.getServer().sendToAllTCP(resp);
        ConsoleRunnable.println("[SPAWN] - Send ENTITIES_UPDATE");
        return 0;
    }

}
