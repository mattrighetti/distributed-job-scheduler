package ds.loadbalancer;

import ds.common.Job;
import ds.common.JobDao;
import ds.common.MapDao;
import ds.common.Message;
import ds.common.Utils.StreamUtils;
import ds.common.Utils.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static ds.common.Message.MessageType.*;
import static ds.common.Message.MessageType.RES_REQ;
import static ds.common.Utils.Strings.NULL;

public class ReverseProxy implements LBMessageHandler {
    private final int maxNumNodes = System.getenv().containsKey("MAX_NUM_NODES") ?
            Integer.parseInt(System.getenv("MAX_NUM_NODES")) : 5;
    private final int listeningPort;
    private final Map<NodeHandler, Integer> nodesInfo;
    private final MapDao<String, String> jobResults;
    private final Map<NodeHandler, List<String>> nodeResultRequests;
    private final JobDao globalJobDeque;
    private final Timer timer;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final ExecutorService incomingConnectionsExecutor = Executors.newFixedThreadPool(maxNumNodes);

    private static final Logger log = LogManager.getLogger(ReverseProxy.class.getName());

    public ReverseProxy(int listeningPort) {
        this.listeningPort = listeningPort;
        this.nodesInfo = new ConcurrentHashMap<>();
        this.jobResults = new MapDao<>("./ReverseProxyJobResults");
        this.nodeResultRequests = new ConcurrentHashMap<>();
        this.globalJobDeque = new JobDao("./ReverseProxyGlobalQueue");
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
                    nodeResultRequests.put(nodeHandler, new ArrayList<>());
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
                handleResultMessage((Message<List<Tuple2<String, String>>>) message, nodeHandler);
                break;
            case RES_REQ:
                handleMixedMessage((Message<Tuple2<List<Tuple2<String, String>>, List<String>>>) message, nodeHandler);
                break;
            case REQUEST_OF_RES:
                handleResultRequestsMessage((Message<List<String>>) message, nodeHandler);
                break;
        }
    }

    private void handleInfoMessage(Message<Integer> message, NodeHandler nodeHandler) {
        if (message.status == 200) {
            log.info("Received info from {}", nodeHandler);
            log.debug("Message status: {}, type: {}, payload: {}, {}",
                    message.status,
                    message.messageType,
                    message.payload,
                    nodeHandler
            );
            nodesInfo.put(nodeHandler, message.payload);
        } else if (message.status == 500) {
            log.warn("{} has been disconnected, removing nodeHandler.", nodeHandler);
            nodesInfo.remove(nodeHandler);
            nodeResultRequests.remove(nodeHandler);
        }
    }

    private void handleJobMessage(Message<Job> message, NodeHandler nodeHandler) {
        log.info("Received job from {}", nodeHandler);
        log.debug("Message status: {}, type: {}, payload: {}",
                message.status,
                message.messageType,
                message.payload
        );

        jobResults.put(message.payload.jobId, NULL.toString());
        globalJobDeque.addLast(message.payload);
        log.debug("Current number of jobs to dispatch: {}", this.globalJobDeque.size());
    }

    private void handleResultMessage(Message<List<Tuple2<String, String>>> message, NodeHandler nodeHandler) {
        log.info("Received result from {}", nodeHandler);
        log.debug("Message status: {}, type: {}, payload: {}",
                message.status,
                message.messageType,
                message.payload
        );

        nodeResultRequests.get(nodeHandler).clear();

        message.payload.forEach(tuple -> {
            log.debug("Inserting result of Job[{}] in resultsMap", tuple.item1);
            jobResults.put(tuple.item1, tuple.item2);
        });
    }

    private void handleResultRequestsMessage(Message<List<String>> message, NodeHandler nodeHandler) {
        log.info("Received result request from {}", nodeHandler);
        log.debug("Message status: {}, type: {}, payload: {}",
                message.status,
                message.messageType,
                message.payload
        );

        log.info("Updating {} requests", nodeHandler);
        nodeResultRequests.put(nodeHandler, message.payload);
        log.debug("{} needs {}", nodeHandler, message.payload);
    }

    private void handleMixedMessage(Message<Tuple2<List<Tuple2<String, String>>, List<String>>> message,
                                    NodeHandler nodeHandler) {
        log.info("Received result + request from {}", nodeHandler);
        log.debug("Message status: {}, type: {}, payload: {}",
                message.status,
                message.messageType,
                message.payload
        );

        message.payload.item1.forEach(tuple -> {
            log.debug("Inserting result of Job[{}] in resultsMap", tuple.item1);
            jobResults.put(tuple.item1, tuple.item2);
        });

        log.info("Updating {} requests", nodeHandler);
        nodeResultRequests.put(nodeHandler, message.payload.item2);
        log.debug("{} needs {}", nodeHandler, message.payload.item2);
    }

    public void requestResultsRoutine(int period) {
        TimerTask requestResultsTask = new TimerTask() {
            @Override
            public void run() {
                List<String> emptyResults = StreamUtils.emptyResultList(jobResults.getMap());
                String bin1 = emptyResults.isEmpty() ? "0" : "1";

                nodeResultRequests.forEach((nodeHandler, strings) -> {
                    List<Tuple2<String, String>> nodeResultsRequests = StreamUtils.availableResults(strings, jobResults.getMap());

                    String bin2 = nodeResultsRequests.isEmpty() ? "0" : "1";
                    String mask = bin1 + bin2;

                    Message<?> message = null;
                    switch (mask) {
                        case "00":
                            log.debug("All results are stored correctly.");
                            return;
                        case "10":
                            log.debug("Sending result request to {}", nodeHandler);
                            message = new Message<>(200, REQUEST_OF_RES, emptyResults);
                            break;
                        case "01":
                            log.debug("Sending results to {}", nodeHandler);
                            message = new Message<>(200, RESULT, nodeResultsRequests);
                            break;
                        case "11":
                            log.debug("Sending results and results request to {}", nodeHandler);
                            Tuple2<List<Tuple2<String, String>>, List<String>> payloadPackage =
                                    new Tuple2<>(nodeResultsRequests, emptyResults);
                            message = new Message<>(200, RES_REQ, payloadPackage);
                            break;
                    }

                    nodeHandler.write(message);
                });
            }
        };

        timer.schedule(requestResultsTask, 0, period);
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
                        log.debug("Remaining number of jobs to dispatch: {}", globalJobDeque.size());
                    }
                }
            }
        };

        timer.schedule(dispatchTask, 0, period);
    }

    private <T> void dispatchAlgorithm(int max, final Map<T, Integer> integerMap) {
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
