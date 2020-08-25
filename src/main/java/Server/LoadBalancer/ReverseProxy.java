package Server.LoadBalancer;

import Server.Job;
import Server.LBMessageHandler;
import Server.Message;
import Server.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReverseProxy implements LBMessageHandler {
    private final int listeningPort;
    private final Map<NodeHandler, Integer> nodesInfo;
    private final Deque<Job> globalJobDeque;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final ExecutorService incomingConnectionsExecutor = Executors.newFixedThreadPool(5);

    private static final Logger log = LogManager.getLogger(ReverseProxy.class.getName());


    public ReverseProxy(int listeningPort) {
        this.listeningPort = listeningPort;
        this.nodesInfo = new ConcurrentHashMap<>();
        this.globalJobDeque = new ArrayDeque<>();
    }

    public void stop() {
        this.isStopped.set(false);
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

                    nodesInfo.put(nodeHandler, -1);
                    this.incomingConnectionsExecutor.submit(nodeHandler);
                }
            } catch (IOException e) {
                log.error("Encountered an error while working with a socket.");
                e.printStackTrace();
            }
        });
    }

    @Override
    public <T> void handleMessage(Message<T> message, NodeHandler nodeHandler) {
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
                log.debug("Message status: {}, type: {}, payload: {}",
                        message.status,
                        message.messageType,
                        message.payload
                );
                nodesInfo.put(nodeHandler, (int) message.payload);
        }
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
