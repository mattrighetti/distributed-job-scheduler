package Server.Network.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketService {
    private ServerSocket serverSocket;
    private final int port;
    private final ServerService<Socket> worker;

    private static final Logger log = LogManager.getLogger(ServerSocketService.class.getName());

    public ServerSocketService(int port, ServerService<Socket> worker) {
        this.port = port;
        this.worker = worker;
    }

    public void listenForConnections() {
        try {
            serverSocket = new ServerSocket(port);
            log.info("Server is listening for incoming connections on port " + this.port);
            log.debug("Socket: {}", serverSocket);
        } catch (IOException e) {
            log.error("Could not listen on port " + this.port);
            System.exit(-1);
        }

        while (true) {
            Socket clientSocket;
            try {
                clientSocket = serverSocket.accept();
                log.debug("Incoming connection {}", clientSocket);
                worker.handle(clientSocket);
                log.info("New PeerWorker has been created -> {}", worker);
            } catch (IOException e) {
                log.error("Accept failed on port " + this.port);
                System.exit(-1);
            }
        }
    }

}
