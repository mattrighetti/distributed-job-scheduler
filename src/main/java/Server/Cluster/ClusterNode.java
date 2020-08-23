package Server.Cluster;

import Server.Message;
import Server.MessageHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClusterNode implements MessageHandler {
    private LoadBalancerHandler loadBalancerHandler;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private static final Logger log = LogManager.getLogger(ClusterNode.class.getName());

    public void connect(String hostname, int port) {
        try {
            Socket socket = new Socket(hostname, port);
            this.loadBalancerHandler = new LoadBalancerHandler(socket, this);
            this.executorService.submit(this.loadBalancerHandler);
        } catch (IOException e) {
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

    public static void main(String[] args) {
        if (args.length > 1) {
            ClusterNode node = new ClusterNode();
            node.connect(args[0], Integer.parseInt(args[1]));
        } else {
            log.fatal("No hostname:port was given, exiting.");
            System.exit(-1);
        }
    }
}
