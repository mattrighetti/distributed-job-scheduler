package Server.Network;

import Server.TrackerServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

public class Peer {
    private final String hostname;
    private final int socketPort;
    private Socket serverSocket;
    private static final Logger log = LogManager.getLogger(Peer.class.getName());

    public Peer(String hostname, int socketPort) {
        this.hostname = hostname;
        this.socketPort = socketPort;
    }

    public void contactTracker() {
        try {
            this.serverSocket = new Socket(this.hostname, this.socketPort);
        } catch (IOException ioException) {
            System.err.println("Couldn't establish connection to tracker.");
            System.exit(-1);
        }
    }

    public Socket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(Socket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            Peer peer = new Peer("localhost", Integer.parseInt(args[0]));
            peer.contactTracker();
        } else {
            log.fatal("No port was found as import parameter.\nExiting program.");
            System.exit(-1);
        }
    }
}
