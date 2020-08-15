package Server.Network.Peer;

import Server.Network.Services.P2PHandler;
import Server.Network.SocketService;
import Server.Network.SocketWrapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PeerSocketService extends SocketService {
    private ConcurrentMap<String, SocketWrapper> peersSockets;

    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private static final Logger log = LogManager.getLogger(PeerSocketService.class.getName());

    public PeerSocketService(int serverPort) {
        super(serverPort);
        this.peersSockets = new ConcurrentHashMap<>();
    }

    /**
    * listenForIncomingConnections opens the specified serverPort and will listen for incoming connections.
     */
    public void listenForIncomingConnections() {
        this.threadPool.execute(() -> {
            openServerSocket();

            while (!isStopped()) {
                Socket clientSocket;
                SocketWrapper socketWrapper;
                try {
                    clientSocket = this.serverSocket.accept();
                    clientSocket.setKeepAlive(true);
                    clientSocket.setSoTimeout(5000);

                    socketWrapper = new SocketWrapper(clientSocket);
                } catch (IOException e) {
                    if (isStopped()) {
                        log.debug("Server Stopped.");
                        return;
                    }
                    throw new RuntimeException("Error accepting client connection", e);
                }

                this.threadPool.execute(new P2PHandler(socketWrapper));
            }

            this.threadPool.shutdown();
            log.info("Server Stopped.");
        });
    }

    public void connectTo(String hostAddress, int serverPort) {
        Socket socket;
        SocketWrapper socketWrapper;
        try {
            socket = new Socket(hostAddress, serverPort);
            socket.setKeepAlive(true);
            socket.setSoTimeout(5000);

            socketWrapper = new SocketWrapper(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
