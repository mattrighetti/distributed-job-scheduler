package Server.Network;

import Server.Network.Services.SocketRunnable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class ServerSocketService {
    private final int serverPort;
    private boolean isStopped;
    private ServerSocket serverSocket;
    protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

    private static final Logger log = LogManager.getLogger(ServerSocketService.class.getName());

    public ServerSocketService(int serverPort) {
        this.serverPort = serverPort;
        this.isStopped = false;
    }

    public void listenForIncomingConnections(SocketRunnable runnable) {
        this.threadPool.execute(() -> {
            openServerSocket();

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

                runnable.setSocket(clientSocket);
                this.threadPool.execute(runnable);
            }

            this.threadPool.shutdown();
            System.out.println("Server Stopped.");
        });
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            log.error("Encountered an error while closing serverSocket.");
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            log.debug("Opening port {}", this.serverPort);
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
}
