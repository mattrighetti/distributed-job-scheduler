package Server.Network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Peer {
    private final String hostname;
    private final int socketPort;
    private static final Logger log = LogManager.getLogger(Peer.class.getName());

    public Peer(String hostname, int socketPort) {
        this.hostname = hostname;
        this.socketPort = socketPort;
    }

    public void contactTracker() {
        try (
                Socket serverSocket = new Socket(this.hostname, this.socketPort);
                BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()))
        ) {
            String fromServer;
            while ((fromServer = in.readLine()) != null) {
                System.out.println(fromServer);
            }
        } catch (IOException ioException) {
            System.err.println("Couldn't establish connection to tracker.");
            System.exit(-1);
        }
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
