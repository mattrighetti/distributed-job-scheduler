package Server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TrackerServer {
    private ServerSocket server;
    private int port;
    private static final Logger log = LogManager.getLogger(TrackerServer.class.getName());

    public TrackerServer(int port) {
        this.port = port;
    }

    private static class PeerWorker implements Runnable {
        private final Socket peerSocket;
        private static final Logger log = LogManager.getLogger(PeerWorker.class.getName());

        public PeerWorker(Socket peerSocket) {
            this.peerSocket = peerSocket;
        }

        public void run() {
            log.trace("PeerWorker run method is starting");
            log.debug("PeerSocket.getInetAddress() = {}", peerSocket.getInetAddress());
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder;
            stringBuilder = new StringBuilder();

            stringBuilder
                    .append(getClass().getName())
                    .append("[")
                    .append("peerSocket: ")
                    .append(this.peerSocket)
                    .append("]");

            return stringBuilder.toString();
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void listenSocket() {
        try {
            this.server = new ServerSocket(this.port);
            log.info("Server is listening for incoming connections on port " + this.port);
        } catch (IOException e) {
            log.error("Could not listen on port " + this.port);
            System.exit(-1);
        }

        while (true) {
            PeerWorker pworker;
            try {
                pworker = new PeerWorker(server.accept());
                log.info("New PeerWorker has been created -> {}", pworker);
                Thread t = new Thread(pworker);
                t.start();
            } catch (IOException e) {
                log.error("Accept failed on port " + this.port);
                System.exit(-1);
            }
        }
    }

    public static void main(String[] args) {
        TrackerServer server = new TrackerServer(8080);
        if (args.length > 0) {
            server.setPort(Integer.parseInt(args[0]));
        }

        server.listenSocket();
    }
}
