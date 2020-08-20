package Server.LoadBalancer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReverseProxy {
    private final int listeningPort;
    private final ArrayList<ClientHandler> clients;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final ExecutorService incomingConnectionsExecutor = Executors.newFixedThreadPool(5);

    private static final Logger log = LogManager.getLogger(ReverseProxy.class.getName());


    public ReverseProxy(int listeningPort) {
        this.listeningPort = listeningPort;
        this.clients = new ArrayList<>();
    }

    public void stop() {
        this.isStopped.set(false);
    }

    public AtomicBoolean isStopped() {
        return isStopped;
    }

    public void openSocket() {
        this.incomingConnectionsExecutor.execute(() -> {
            try (
                    ServerSocket serverSocket = new ServerSocket(this.listeningPort)
            ) {
                Socket clientSocket;
                ClientHandler clientHandler;
                log.info("Listening for incoming client connections");
                while (this.isStopped.get()) {
                    clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(30 * 1000);
                    clientSocket.setKeepAlive(true);
                    log.debug("New client with address {} is connecting", clientSocket.getInetAddress());
                    clientHandler = new ClientHandler(clientSocket);
                    
                    clients.add(clientHandler);
                    this.incomingConnectionsExecutor.submit(clientHandler);
                }
            } catch (IOException e) {
                log.error("Encountered an error while working with a socket.");
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        ReverseProxy reverseProxy;
        if (args.length > 1) {
            reverseProxy = new ReverseProxy(Integer.parseInt(args[1]));
        } else {
            reverseProxy = new ReverseProxy(8080);
        }

        reverseProxy.openSocket();
    }
}
