package Server.Network.Tracker;

import Server.Network.Peer.PeerSocketService;
import Server.Network.Services.SocketRunnable;
import Server.Network.SocketService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TrackerSocketService extends SocketService {
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private static final Logger log = LogManager.getLogger(PeerSocketService.class.getName());

    public TrackerSocketService(int serverPort) {
        super(serverPort);
    }

    /**
     * listenForIncomingConnections opens the specified serverPort and will listen for incoming connections.
     * As soon as a connection is received this method will set the new Socket to the SocketRunnable object
     * and it will execute it.
     *
     * @param incomingConnectionTask Runnable that contains a task to run whenever a new connection is received
     */
    public void listenForIncomingConnections(SocketRunnable incomingConnectionTask) {
        this.threadPool.execute(() -> {
            super.openServerSocket();

            while (!isStopped()) {
                Socket clientSocket;
                try {
                    clientSocket = this.serverSocket.accept();
                } catch (IOException e) {
                    if (isStopped()) {
                        log.debug("Server Stopped.");
                        return;
                    }
                    throw new RuntimeException("Error accepting client connection", e);
                }

                incomingConnectionTask.setSocket(clientSocket);
                this.threadPool.execute(incomingConnectionTask);
            }

            this.threadPool.shutdown();
            log.info("Server Stopped.");
        });
    }
}
