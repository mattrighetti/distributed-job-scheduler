package ds.cluster;

import ds.common.*;
import ds.common.Utils.HashGenerator;
import ds.common.Utils.StreamUtils;
import ds.common.Utils.Tuple2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static ds.common.Message.MessageType.*;

public class ClusterNode implements MessageHandler, ClientSubmissionHandler {
    private LoadBalancerHandler loadBalancerHandler;
    private final AtomicBoolean isStopped = new AtomicBoolean(false);
    private final JobDao localJobDeque;
    private final MapDao<String, Optional<String>> resultsMap;
    private final List<String> loadBalancerResultRequestList;
    private final Timer timer;
    private final Executor executor;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private static final Logger log = LogManager.getLogger(ClusterNode.class.getName());

    public ClusterNode() {
        this.localJobDeque = new JobDao("./ClusterNodeLocalJobDeque");
        this.resultsMap = new MapDao<>("./ClusterNodeResultsMap");
        this.loadBalancerResultRequestList = new ArrayList<>();
        this.timer = new Timer();
        this.executor = new Executor(localJobDeque, resultsMap.getMap());
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

    public void listenForClientConnections(int port, boolean enablePiping) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            Socket clientSocket;
            while (!this.isStopped.get()) {
                clientSocket = serverSocket.accept();
                log.debug("Received connection from {}", clientSocket.getInetAddress());
                this.executorService.execute(new ClientHandler(clientSocket, this, enablePiping));
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

    public void requestResults() {
        TimerTask requestResultsTask = new TimerTask() {
            @Override
            public void run() {
                List<String> emptyResults = StreamUtils.emptyResultList(resultsMap.getMap());
                List<Tuple2<String, String>> lbResultRequest =
                        StreamUtils.availableResults(loadBalancerResultRequestList, resultsMap.getMap());

                lbResultRequest.forEach(tuple -> loadBalancerResultRequestList.remove(tuple.item1));

                String bin1 = emptyResults.isEmpty() ? "0" : "1";
                String bin2 = lbResultRequest.isEmpty() ? "0" : "1";
                String mask = bin1 + bin2;

                Message<?> message = null;
                switch (mask) {
                    case "00":
                        log.debug("All results are stored correctly.");
                        message = new Message<>(200, REQUEST_OF_RES, new ArrayList<>());
                        break;
                    case "10":
                        log.info("Sending result request to loadbalancer.");
                        message = new Message<>(200, REQUEST_OF_RES, emptyResults);
                        break;
                    case "01":
                        log.info("Sending results to loadbalancer.");
                        message = new Message<>(200, RESULT, lbResultRequest);
                        break;
                    case "11":
                        log.info("Sending results and results request to loadbalancer.");
                        Tuple2<List<Tuple2<String, String>>, List<String>> payloadPackage =
                                new Tuple2<>(lbResultRequest, emptyResults);
                        message = new Message<>(200, RES_REQ, payloadPackage);
                        break;
                }

                loadBalancerHandler.write(message);
            }
        };

        timer.schedule(requestResultsTask, 0, 3 * 1000);
    }

    public void runExecutor() {
        log.info("Running executor");
        this.executor.triggerTimerCheck(1000);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void handleMessage(Message<T> message) {
        switch (message.messageType) {
            case JOB:
                handleJobMessage((Message<Job>) message);
                break;
            case RESULT:
                handleResultMessage((Message<List<Tuple2<String, String>>>) message);
                break;
            case RES_REQ:
                handleMixedMessage((Message<Tuple2<List<Tuple2<String, String>>, List<String>>>) message);
                break;
            case REQUEST_OF_RES:
                handleResultRequestsMessage((Message<List<String>>) message);
                break;
            default:
                log.warn("Received a response with MessageType {}", message.messageType);
                log.error("This response cannot be handled properly. Method hasn't been implemented.");
                break;
        }
    }

    public void handleJobMessage(Message<Job> message) {
        log.info("Received job from server");
        this.localJobDeque.add(message.payload);
    }

    public void handleResultMessage(Message<List<Tuple2<String, String>>> message) {
        log.info("Received a result request from server");
        List<Tuple2<String, String>> resultList = message.payload;

        log.info("Resetting loadBalancerResultRequestList");
        loadBalancerResultRequestList.clear();
        log.debug("Updated LBRequest after RESULT: {}", loadBalancerResultRequestList);

        resultList.forEach(result -> {
            log.debug("Inserting result of Job[{}] in resultsMap", result.item1);
            resultsMap.put(result.item1, Optional.of(result.item2));
        });
    }

    public void handleResultRequestsMessage(Message<List<String>> message) {
        log.info("Received a result request from server");
        List<String> requestList = message.payload;

        log.info("Resetting loadBalancerResultRequestList");
        loadBalancerResultRequestList.clear();
        loadBalancerResultRequestList.addAll(requestList);
        log.debug("Updated LBRequest: {}", loadBalancerResultRequestList);
    }

    public void handleMixedMessage(Message<Tuple2<List<Tuple2<String, String>>, List<String>>> mixedMessage) {
        log.info("Received a result + request from server");
        List<Tuple2<String, String>> resultList = mixedMessage.payload.item1;
        List<String> requestList = mixedMessage.payload.item2;

        resultList.forEach(result -> {
            log.debug("Inserting result of Job[{}] in resultsMap", result.item1);
            resultsMap.put(result.item1, Optional.of(result.item2));
        });

        log.info("Resetting loadBalancerResultRequestList");
        loadBalancerResultRequestList.clear();
        loadBalancerResultRequestList.addAll(requestList);
        log.debug("Updated LBRequest: {}", loadBalancerResultRequestList);
    }

    @Override
    public String handleJobSubmission(int milliseconds) {
        String ticketHash = HashGenerator.generateHash(16);
        Message<Job> jobMessage = new Message<>(200, JOB, new Job(ticketHash, milliseconds));
        loadBalancerHandler.write(jobMessage);
        resultsMap.put(ticketHash, Optional.empty());
        return ticketHash;
    }

    @Override
    public Optional<String> handleResultRequest(String resultHash) {
        if (resultsMap.containsKey(resultHash)) {
            return resultsMap.get(resultHash);
        } else {
            return Optional.of("This job could not be found on this node.");
        }
    }
}
