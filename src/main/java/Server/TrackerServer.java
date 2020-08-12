package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TrackerServer {
    private ServerSocket server;

    private static class PeerWorker implements Runnable {
        private final Socket peerSocket;

        public PeerWorker(Socket peerSocket) {
            this.peerSocket = peerSocket;
        }

        public void run() {
            System.out.print("Peer worker is running on port");
            System.out.println("\tpeerSocket.getInetAddress() = " + peerSocket.getInetAddress());
        }
    }

    public void listenSocket() {
        try {
            this.server = new ServerSocket(8080);
        } catch (IOException e) {
            System.out.println("Could not listen on port 4444");
            System.exit(-1);
        }

        while (true) {
            PeerWorker pworker;
            try {
                pworker = new PeerWorker(server.accept());
                Thread t = new Thread(pworker);
                t.start();
            } catch (IOException e) {
                System.out.println("Accept failed: 8080");
                System.exit(-1);
            }
        }
    }

    public static void main(String[] args) {
        TrackerServer server = new TrackerServer();
        server.listenSocket();
    }
}
