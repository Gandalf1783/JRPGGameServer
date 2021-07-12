package de.gandalf1783.gameserver.networkhandler;

import com.esotericsoftware.kryonet.Connection;
import de.gandalf1783.gameserver.objects.BasicRequest;

public class BasicRequestExecutor {

    public BasicRequestExecutor(String command) {
        BasicRequestHandler.registerResponse(command, this);
    }

    public void execute(BasicRequest req, Connection conn) {

    }

}
