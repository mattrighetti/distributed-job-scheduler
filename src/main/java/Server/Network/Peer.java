package Server.Network;

//import jdk.jfr.internal.LogLevel;
//import jdk.jfr.internal.LogTag;
//import jdk.jfr.internal.Logger;

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
        // Logger.log(LogTag.JFR_EVENT, LogLevel.DEBUG, "Establishing connection to tracker server");
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
    }
}
