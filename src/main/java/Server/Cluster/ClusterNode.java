package Server.Cluster;

import Server.Message;
import Server.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClusterNode implements MessageHandler, ClientSubmissionHandler {
    private LoadBalancerHandler loadBalancerHandler;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private static final Logger log = LogManager.getLogger(ClusterNode.class.getName());

    public void stop() {
        this.isStopped.set(true);
    }

    public void connect(String hostname, int port) {
        try {
            Socket socket = new Socket(hostname, port);
            this.loadBalancerHandler = new LoadBalancerHandler(socket, this);
            this.executorService.submit(this.loadBalancerHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listenForClientConnections(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket;
            while (!this.isStopped.get()) {
                clientSocket = serverSocket.accept();
                log.debug("Received connection from {}", clientSocket.getInetAddress());
                this.executorService.execute(new ClientHandler(clientSocket, this));
            }
        } catch (IOException e) {
            log.error("Encountered error while listening for clients.");
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(Message<?> message) {
        switch (message.messageType) {
            case INFO:
                log.info("Received info from server");
                // TODO print info message (?) this is probably never going to be needed
            case JOB:
                log.info("Received job from server");
                // TODO add job to executor queue
        }
    }

    @Override
    public String handleJobSubmission(int milliseconds) {
        int count = 16;
        final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        // TODO forward job to ReverseProxy with ticket hash so that it can be retrieved at will
        return builder.toString();
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            ClusterNode node = new ClusterNode();
            node.connect(args[0], Integer.parseInt(args[1]));
            node.listenForClientConnections(9000);
        } else {
            log.fatal("No hostname:port was given, exiting.");
            System.exit(-1);
        }
    }
}
