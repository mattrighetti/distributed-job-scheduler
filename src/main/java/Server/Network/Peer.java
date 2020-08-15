package Server.Network;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;

public class Peer {
    private ServerSocketService serverSocketService;
    private final ServerService<Socket> peerWorker = socket -> {
        log.debug("Received connection from Socket: {}", socket);
    };

    private static final Logger log = LogManager.getLogger(Peer.class.getName());

    public void contactTracker(String trackerHostname, int trackerSocketPort) {
        try (
                Socket serverSocket = new Socket(trackerHostname, trackerSocketPort);
                BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        ) {
            String fromServer = in.readLine();
            log.debug("Reading from server JSON: {}", fromServer);
            if (fromServer != null) {
                List<String> ipAddresses = new Gson().fromJson(fromServer, List.class);
                log.debug("Parsing JSON to List<String> –> {}", ipAddresses);
                connectToPeers(ipAddresses);
            } else {
                log.fatal("Received empty String from TrackerServer.\n Exiting program.");
                System.exit(-1);
            }
        } catch (IOException ioException) {
            System.err.println("Couldn't establish connection to tracker.\n Exiting program.");
            System.exit(-1);
        }
    }

    public void startListeningForPeers() {
        serverSocketService = new ServerSocketService(8081);
    }

    public void connectToPeers(List<String> ipAddresses) {
        if (!ipAddresses.isEmpty()) {
            ipAddresses.forEach(ip -> {
                log.debug("Connecting {} to Peer({})", this, ip);
                //serverSocketService.connectToPeer(ip, 90);
            });
        } else {
            log.debug("List is empty.");
        }
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            Peer peer = new Peer();
            log.info("{} listening for other peers", peer);
            peer.startListeningForPeers();
            log.info("Connecting to Tracker at {}:{}", args[0], args[1]);
            peer.contactTracker(args[0], Integer.parseInt(args[1]));
        } else {
            log.fatal("No port was found as import parameter.\nExiting program.");
            System.exit(-1);
        }
    }
}
