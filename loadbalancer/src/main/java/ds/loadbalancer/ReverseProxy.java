package ds.loadbalancer;

import ds.common.Job;
import ds.common.Message;
import ds.common.Utils.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static ds.common.Message.MessageType.JOB;

public class ReverseProxy implements LBMessageHandler {
    private final int listeningPort;
    private final Map<NodeHandler, Integer> nodesInfo;
    private final Map<String, String> jobResults;
    private final Map<NodeHandler, List<String>> nodeResultRequests;
    private final Deque<Job> globalJobDeque;
    private final Timer timer;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final ExecutorService incomingConnectionsExecutor = Executors.newFixedThreadPool(5);

    private static final Logger log = LogManager.getLogger(ReverseProxy.class.getName());

    public ReverseProxy(int listeningPort) {
        this.listeningPort = listeningPort;
        this.nodesInfo = new ConcurrentHashMap<>();
        this.jobResults = new ConcurrentHashMap<>();
        this.nodeResultRequests = new ConcurrentHashMap<>();
        this.globalJobDeque = new ConcurrentLinkedDeque<>();
        this.timer = new Timer();
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
                    log.debug("Added new node {} to nodesInfo CHashMap", nodeHandler);
                    this.incomingConnectionsExecutor.submit(nodeHandler);
                }
            } catch (IOException e) {
                log.error("Encountered an error while working with a socket.");
                e.printStackTrace();
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void handleMessage(Message<T> message, NodeHandler nodeHandler) {
        switch (message.messageType) {
            case INFO:
                handleInfoMessage((Message<Integer>) message, nodeHandler);
                break;
            case JOB:
                handleJobMessage((Message<Job>) message, nodeHandler);
                break;
            case RESULT:
                handleResultMessage((Message<List<Tuple2<String, String>>>) message);
                break;
            case RES_REQ:
                handleMixedMessage((Message<Tuple2<List<Tuple2<String, String>>, List<String>>>) message, nodeHandler);
                break;
            case REQUEST_OF_RES:
                handleResultRequestsMessage((Message<List<String>>) message, nodeHandler);
                break;
        }
    }

    public void handleInfoMessage(Message<Integer> message, NodeHandler nodeHandler) {
        log.info("Received info on node's queue");
        log.debug("Message status: {}, type: {}, payload: {}, {}",
                message.status,
                message.messageType,
                message.payload,
                nodeHandler
        );
        nodesInfo.put(nodeHandler, message.payload);
    }

    public void handleJobMessage(Message<Job> message, NodeHandler nodeHandler) {
        log.info("Received job from node.");
        log.debug("Message status: {}, type: {}, payload: {}",
                message.status,
                message.messageType,
                message.payload
        );
        globalJobDeque.addLast(message.payload);
        log.debug("Current number of jobs to dispatch: {}", this.globalJobDeque.size());
    }

    public void handleResultMessage(Message<List<Tuple2<String, String>>> message) {
        log.info("Received result from node.");
        log.debug("Message status: {}, type: {}, payload: {}",
                message.status,
                message.messageType,
                message.payload
        );

        message.payload.forEach(tuple -> jobResults.put(tuple.item1, tuple.item2));
    }

    public void handleResultRequestsMessage(Message<List<String>> message, NodeHandler nodeHandler) {
        log.info("Received result request from node.");
        log.debug("Message status: {}, type: {}, payload: {}",
                message.status,
                message.messageType,
                message.payload
        );

        nodeResultRequests.put(nodeHandler, message.payload);
    }

    public void handleMixedMessage(Message<Tuple2<List<Tuple2<String, String>>, List<String>>> message,
                                   NodeHandler nodeHandler) {
        log.info("Received result + request from node.");
        log.debug("Message status: {}, type: {}, payload: {}",
                message.status,
                message.messageType,
                message.payload
        );

        message.payload.item1.forEach(tuple -> jobResults.put(tuple.item1, tuple.item2));
        nodeResultRequests.put(nodeHandler, message.payload.item2);
    }

    public void dispatch(int period, int maxNumOfJobs) {
        TimerTask dispatchTask = new TimerTask() {
            @Override
            public void run() {
                if (nodesInfo.isEmpty()) {
                    log.debug("No node is connected at the moment, can't dispatch jobs.");
                } else {
                    log.debug("Nodes available: {}", nodesInfo);
                    if (!globalJobDeque.isEmpty()) {
                        dispatchAlgorithm(Math.min(maxNumOfJobs, globalJobDeque.size()), nodesInfo);
                    }
                }
            }
        };

        timer.schedule(dispatchTask, 0, period);
    }

    public <T> void dispatchAlgorithm(int max, final Map<T, Integer> integerMap) {
        List<Tuple2<Integer, T>> list = Dispatcher.convertNodesInfoToList(integerMap);
        Dispatcher.applyAlgorithmFunction(max, (max_value) -> {
            Job jobToDispatch;
            while (max_value > 0) {
                jobToDispatch = this.globalJobDeque.removeFirst();
                ((NodeHandler) list.get(0).item2).write(new Message<>(200, JOB, jobToDispatch));
                list.get(0).item1 += 1;
                list.sort(Comparator.comparingInt(o -> o.item1));
                max_value--;
            }
        });
    }
}
