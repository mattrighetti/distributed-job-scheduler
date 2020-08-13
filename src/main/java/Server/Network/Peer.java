package Server.Network;

import java.io.IOException;
import java.net.Socket;

public class Peer {
    private final String hostname;
    private final int socketPort;
    private Socket serverSocket;

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
        Peer peer = new Peer("localhost", 8080);
        peer.contactTracker();
        while (true);
    }
}
