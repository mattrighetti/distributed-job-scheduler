package Server.Network.Services;

import Server.Network.PeerWorker;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PeerHandler extends SocketRunnable {
    private final List<String> peersIpAddresses;

    private static final Logger log = LogManager.getLogger(PeerWorker.class.getName());

    public PeerHandler(List<String> peersIpAddresses) {
        this.peersIpAddresses = new ArrayList<>(peersIpAddresses);
    }

    @Override
    public void run() {
        log.trace("PeerWorker run method is starting");
        log.debug("peerSocket -> {}", socket);
        try (
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
}
