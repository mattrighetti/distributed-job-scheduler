package Server.Network;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Class that implements a full-duplex socket wrapper
 */
public class SocketWrapper {
    private final OutputStreamWriter out;
    private final BufferedReader in;
    private final Socket conn;
    private final ExecutorService executorService = Executors.newFixedThreadPool(3);

    private static final Logger log = LogManager.getLogger(SocketWrapper.class.getName());

    public SocketWrapper(Socket conn) throws IOException {
        this.conn = conn;
        this.out = new OutputStreamWriter(conn.getOutputStream(), StandardCharsets.UTF_8);
        this.in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    }

    public Future<String> read() {
        return this.executorService.submit(() -> {
            String readString = null;
            try {
                log.info("Reading...");
                readString = in.readLine();
                log.info("received -> {}", readString);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return readString;
        });
    }

    public void write(String dataString) {
        this.executorService.execute(() -> {
            try {
                log.debug("Sending -> {}", dataString);
                out.write(dataString);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Closes Socket connection and input and output streams
     */
    public void close() {
        try {
            conn.close();
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isClosed() {
        return conn.isClosed();
    }
}
