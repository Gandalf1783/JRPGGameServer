package de.gandalf1783.gameserver.networkhandler;

import com.esotericsoftware.kryonet.Connection;
import de.gandalf1783.gameserver.objects.BasicRequest;

import java.util.HashMap;

public class BasicRequestHandler {


    private static final HashMap<String, BasicRequestExecutor> executorHashMap = new HashMap<>();


    public static void executeResponse(BasicRequest req, Connection conn) {
        if(!executorHashMap.containsKey(req.text)) {
            System.out.println("Request \""+req.text+"\" is not known.");
            return;
        }
        executorHashMap.get(req.text).execute(req, conn);
    }

    public static void registerResponse(String command, BasicRequestExecutor executor) {
        executorHashMap.put(command, executor);
        System.out.println("Registered Response for \""+command+"\"");
    }
}
