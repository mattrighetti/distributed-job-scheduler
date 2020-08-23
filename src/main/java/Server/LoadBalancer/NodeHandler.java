package Server.LoadBalancer;

import Server.Message;
import Server.MessageHandler;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class NodeHandler implements Callable<Void> {
    private final Socket clientSocket;
    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStreamWriter;
    private final AtomicBoolean isStopped;
    private final MessageHandler messageHandler;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private static final Logger log = LogManager.getLogger(NodeHandler.class.getName());

    public NodeHandler(Socket clientSocket, MessageHandler messageHandler) {
        this.clientSocket = clientSocket;
        this.isStopped = new AtomicBoolean(false);
        this.messageHandler = messageHandler;
    }

    public void initSocket() {
        try {
            log.info("Init BufferedReader and OutputStreamWriter");
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            this.outputStreamWriter = new OutputStreamWriter(this.clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read() {
        this.executorService.execute(() -> {
            try {
                while (!this.isStopped.get()) {
                    String inputJSON = this.bufferedReader.readLine();

                    if (inputJSON == null) {
                        log.debug("Received null, closing socket.");
                        this.stop();
                    }

                    log.debug("Received JSON: {}", inputJSON);
                    messageHandler.handleMessage(new Gson().fromJson(inputJSON, Message.class));
                }
            } catch (SocketTimeoutException e) {
                log.debug("No message was received for 30 seconds, closing connection...");
                this.stop();
            } catch (IOException e) {
                log.error("Encountered error while reading from inputStream of {}", this.clientSocket.getInetAddress());
                e.printStackTrace();
            }
        });
    }

    public void write(Message<?> message) {
        log.info("Writing message to outputStream");
        String json = new Gson().toJson(message);
        try {
            this.outputStreamWriter.write(json + '\n');
            this.outputStreamWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        this.isStopped.set(true);
        this.closeConnections();
    }

    public void closeConnections() {
        try {
            log.info("Closing input streams and socket");
            this.outputStreamWriter.close();
            this.bufferedReader.close();
            this.clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Void call() throws Exception {
        this.initSocket();
        this.read();
        this.write(new Message<>(200, Message.MessageType.INFO, -1));
        return null;
    }
}
