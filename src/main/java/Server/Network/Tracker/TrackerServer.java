package Server.Network.Tracker;

import Server.Network.Services.P2THandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * TrackerServer is the central server that each peer know of and can contact
 * to get other peers' ip addresses.
 *
 * Each time a Peer contacts the TrackerServer it requests ips and the TrackerServer
 * adds its ip to a list of addresses.
 */
public class TrackerServer implements TrackerServerUtils {
    private int port;
    private final List<String> peersIpAddresses = new ArrayList<>();

    private static final Logger log = LogManager.getLogger(TrackerServer.class.getName());

    /**
     * TrackerServer constructor initiates the TrackerServer that will listen for peers on the specified port
     * @param port port to listen on
     */
    public TrackerServer(int port) {
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * initServerSocket opens the ServerSocket connection and starts listening for other peers
     */
    public void initServerSocket() {
        TrackerSocketService trackerSocketService = new TrackerSocketService(8080);
        trackerSocketService.listenForIncomingConnections(new P2THandler(this));
    }

    /**
     * addIpToArrayList adds a given ip address to the TrackerServer's ip addresses list
     * @param ipAddress
     */
    @Override
    public synchronized void addIpToArrayList(String ipAddress) {
        if (!peersIpAddresses.contains(ipAddress)) {
            log.debug("Adding {} to peersIpAddresses", ipAddress);
            peersIpAddresses.add(ipAddress);
        } else {
            log.debug("{} is already present in peersIpAddresses", ipAddress);
        }
    }

    @Override
    public synchronized List<String> getIpAddresses() {
        return this.peersIpAddresses;
    }

    public static void main(String[] args) {
        TrackerServer server = new TrackerServer(8080);
        if (args.length > 0) {
            server.setPort(Integer.parseInt(args[0]));
        }

        server.initServerSocket();
    }
}
