package de.gandalf1783.gameserver.networkhandler;

import com.esotericsoftware.kryonet.Connection;
import de.gandalf1783.gameserver.core.Main;
import de.gandalf1783.gameserver.listener.ClientInteractions;
import de.gandalf1783.gameserver.objects.BasicRequest;
import de.gandalf1783.gameserver.threads.ConsoleRunnable;
import de.gandalf1783.gameserver.threads.GenerationQueueThread;

public class RequestChunkExecutor extends BasicRequestExecutor {

    public RequestChunkExecutor(String command) {
        super(command);
    }

    @Override
    public void execute(BasicRequest req, Connection conn) {
        String[] data = req.data.split("#");
        if(data.length == 2) {
            int chunkX = Integer.parseInt(data[0]);
            int chunkY = Integer.parseInt(data[1]);
            if(Main.getWorldInstance().isChunkGenerated(chunkX, chunkY)) {
                ClientInteractions.sendChunk(chunkX, chunkY, conn);
            } else  {
                GenerationQueueThread.addRequest(chunkX+"#"+chunkY, conn);
            }
        }
    }
}
