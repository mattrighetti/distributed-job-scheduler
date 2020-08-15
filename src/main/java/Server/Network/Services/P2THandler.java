package Server.Network.Services;

import Server.Network.Tracker.TrackerServerUtils;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class P2THandler extends SocketRunnable {
    private final List<String> peersIpAddresses;
    private final TrackerServerUtils trackerServerUtils;

    private static final Logger log = LogManager.getLogger(P2THandler.class.getName());

    public P2THandler(TrackerServerUtils trackerServerUtils) {
        this.trackerServerUtils = trackerServerUtils;
        this.peersIpAddresses = this.trackerServerUtils.getIpAddresses();
    }

    @Override
    public void run() {
        log.trace("PeerWorker run method is starting");
        log.debug("peerSocket -> {}", socket);
        try (
                OutputStreamWriter out = new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8);
        ) {
            String jsonString = new Gson().toJson(peersIpAddresses.toArray());
            log.debug("Sending JSON : {}", jsonString);
            out.write(jsonString);
            out.flush();
            log.info("Closing connection...");
            socket.close();
        } catch (IOException err) {
            log.error("Encountered error while writing to outputStream");
            err.printStackTrace();
        }

        log.debug("Adding {} to ipAddressesList", socket.getInetAddress().getHostAddress());
        trackerServerUtils.addIpToArrayList(socket.getInetAddress().getHostAddress());
    }
}
