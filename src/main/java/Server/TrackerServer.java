package Server;

import Server.Network.Service.PeerWorker;
import Server.Network.Service.ServerService;
import Server.Network.Service.ServerSocketService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TrackerServer {
    private int port;
    private final List<String> peersIpAddresses = new ArrayList<>();

    private final ServerService<Socket> peerHandler = socket -> {
        log.debug("Inserting {} in peersIpAddresses", socket.getInetAddress().getHostAddress());
        Thread t = new Thread(new PeerWorker(socket, peersIpAddresses));
        t.start();

        addIpToArrayList(socket.getInetAddress().getHostAddress());
    };

    private static final Logger log = LogManager.getLogger(TrackerServer.class.getName());

    public TrackerServer(int port) {
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void addIpToArrayList(String ipAddress) {
        if (!peersIpAddresses.contains(ipAddress)) {
            log.debug("Adding {} to peersIpAddresses", ipAddress);
            peersIpAddresses.add(ipAddress);
        } else {
            log.debug("{} is already present in peersIpAddresses", ipAddress);
        }
    }

    public void initServerSocket() {
        ServerSocketService serverSocketService = new ServerSocketService(8080, peerHandler);
        serverSocketService.listenForConnections();
    }

    public static void main(String[] args) {
        TrackerServer server = new TrackerServer(8080);
        if (args.length > 0) {
            server.setPort(Integer.parseInt(args[0]));
        }

        server.initServerSocket();
    }
}
