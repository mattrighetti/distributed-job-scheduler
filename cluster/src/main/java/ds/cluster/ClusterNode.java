package ds.cluster;

import ds.common.Job;
import ds.common.Message;
import ds.common.MessageHandler;
import ds.common.Utils.HashGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static ds.common.Message.MessageType.JOB;
import static ds.common.Message.MessageType.INFO;

public class ClusterNode implements MessageHandler, ClientSubmissionHandler {
    private LoadBalancerHandler loadBalancerHandler;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final Deque<Job> localJobDeque;
    private final Map<String, String> resultsMap;
    private final Timer timer;
    private final Executor executor;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private static final Logger log = LogManager.getLogger(ClusterNode.class.getName());

    public ClusterNode() {
        this.localJobDeque = new ConcurrentLinkedDeque<>();
        this.resultsMap = new HashMap<>();
        this.timer = new Timer();
        this.executor = new Executor(localJobDeque, resultsMap);
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

    public void runExecutor() {
        log.info("Running executor");
        this.executor.triggerTimerCheck(1000);
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
                this.localJobDeque.add((Job) message.payload);
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
}
