package Server.Network;

import Server.Network.Peer.PeerSocketService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketService {
    public final int serverPort;
    public boolean isStopped;
    public ServerSocket serverSocket;

    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private static final Logger log = LogManager.getLogger(PeerSocketService.class.getName());

    public SocketService(int serverPort) {
        this.serverPort = serverPort;
        this.isStopped = false;
    }

    /**
     * Returns whether the server has a listening port active or not
     *
     * @return isStopped value
     */
    public synchronized boolean isStopped() {
        return this.isStopped;
    }

    /**
     * Stops the listening ServerSocket of the server
     */
    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            log.error("Encountered an error while closing serverSocket.");
            throw new RuntimeException("Error closing server", e);
        }
    }

    /**
     * Opens the ServerSocket on the specified port
     */
    public void openServerSocket() {
        try {
            log.debug("Opening port {}", this.serverPort);
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}
