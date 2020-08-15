package Server.Network.Service;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PeerWorker implements Runnable {
    private final Socket peerSocket;
    private final List<String> peersIpAddresses;
    private static final Logger log = LogManager.getLogger(PeerWorker.class.getName());

    public PeerWorker(Socket peerSocket, final List<String> peersIpAddresses) {
        this.peerSocket = peerSocket;
        this.peersIpAddresses = new ArrayList<>(peersIpAddresses);
    }

    public void run() {
        log.trace("PeerWorker run method is starting");
        log.debug("peerSocket -> {}", peerSocket);
        try (
                OutputStreamWriter out = new OutputStreamWriter(peerSocket.getOutputStream(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
        ) {
            String jsonString = new Gson().toJson(peersIpAddresses.toArray());
            log.debug("Sending JSON : {}", jsonString);
            out.write(jsonString);
            out.flush();
            log.info("Closing connection...");
        } catch (IOException err) {
            log.error("Encountered error while writing to outputStream");
            err.printStackTrace();
        }
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
