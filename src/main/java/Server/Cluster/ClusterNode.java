package Server.Cluster;

import Server.Job;
import Server.Message;
import Server.MessageHandler;
import Utils.HashGenerator;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static Server.Message.MessageType.INFO;
import static Server.Message.MessageType.JOB;

public class ClusterNode implements MessageHandler, ClientSubmissionHandler {
    private LoadBalancerHandler loadBalancerHandler;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final Deque<Job> localJobDeque;
    private final Timer timer;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private static final Logger log = LogManager.getLogger(ClusterNode.class.getName());

    public ClusterNode() {
        this.localJobDeque = new ConcurrentLinkedDeque<>();
        this.timer = new Timer();
    }

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

    public void sendJobQueueInfo() {
        TimerTask jobQueueInfoTask = new TimerTask() {
            @Override
            public void run() {
                Message<Integer> localDequeInfoMessage = new Message<>(200, INFO, localJobDeque.size());
                log.info("Sending info message to server");
                log.debug("Message: {}", localDequeInfoMessage);
                loadBalancerHandler.write(localDequeInfoMessage);
            }
        };

        timer.schedule(jobQueueInfoTask, 0, 1000);
    }

    @Override
    public <T> void handleMessage(Message<T> message) {
        switch (message.messageType) {
            case INFO:
                log.error("Received info from server, this is unexpected");
                log.error("Message: {}", message);
                // TODO print info message (?) this is probably never going to be needed
                break;
            case JOB:
                log.info("Received job from server");
                log.debug("Message: {}", message);
                // TODO add job to executor queue
                break;
        }
    }

    @Override
    public String handleJobSubmission(int milliseconds) {
        String ticketHash = HashGenerator.generateHash(16);
        Message<Job> jobMessage = new Message<>(200, JOB, new Job(ticketHash, milliseconds));
        loadBalancerHandler.write(jobMessage);
        return ticketHash;
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            ClusterNode node = new ClusterNode();
            node.connect(args[0], Integer.parseInt(args[1]));
            node.sendJobQueueInfo();
            node.listenForClientConnections(9000);
        } else {
            log.fatal("No hostname:port was given, exiting.");
            System.exit(-1);
        }
    }
}
