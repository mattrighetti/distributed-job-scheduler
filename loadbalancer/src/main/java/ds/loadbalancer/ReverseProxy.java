package ds.loadbalancer;

import ds.common.Job;
import ds.common.Message;
import ds.common.MessageHandler;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReverseProxy implements MessageHandler {
    private final int listeningPort;
    private final ArrayList<NodeHandler> clients;
    private final Deque<Job> globalJobDeque;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final ExecutorService incomingConnectionsExecutor = Executors.newFixedThreadPool(5);

    private static final Logger log = LogManager.getLogger(ReverseProxy.class.getName());


    public ReverseProxy(int listeningPort) {
        this.listeningPort = listeningPort;
        this.clients = new ArrayList<>();
        this.globalJobDeque = new ArrayDeque<>();
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
                NodeHandler nodeHandler;
                log.info("Listening for incoming client connections on port {}", this.listeningPort);
                while (!this.isStopped.get()) {
                    clientSocket = serverSocket.accept();
                    clientSocket.setSoTimeout(30 * 1000);
                    clientSocket.setKeepAlive(true);
                    log.debug("New client with address {} is connecting", clientSocket.getInetAddress());
                    nodeHandler = new NodeHandler(clientSocket, this);
                    
                    clients.add(nodeHandler);
                    this.incomingConnectionsExecutor.submit(nodeHandler);
                }
            } catch (IOException e) {
                log.error("Encountered an error while working with a socket.");
                e.printStackTrace();
            }
        });
    }

    @Override
    public <T> void handleMessage(Message<T> message) {
        log.debug("Message Status: {}", message.status);
        switch (message.messageType) {
            case JOB:
                log.info("Received job from node.");
                log.debug("Message status: {}, type: {}, payload: {}",
                        message.status,
                        message.messageType,
                        message.payload
                );
                this.globalJobDeque.addLast((Job) message.payload);
                log.debug("Current number of jobs to dispatch: {}", this.globalJobDeque.size());
            case INFO:
                log.info("Received info on node's queue");
                // TODO update node's table
        }
    }
}
