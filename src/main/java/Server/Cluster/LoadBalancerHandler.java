package Server.Cluster;

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

public class LoadBalancerHandler implements Callable<Void> {
    private final Socket loadBalancerSocket;
    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStreamWriter;
    private final AtomicBoolean isStopped;
    private final MessageHandler messageHandler;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private static final Logger log = LogManager.getLogger(LoadBalancerHandler.class.getName());

    public LoadBalancerHandler(Socket loadBalancerSocket, MessageHandler messageHandler) {
        this.loadBalancerSocket = loadBalancerSocket;
        this.isStopped = new AtomicBoolean(false);
        this.messageHandler = messageHandler;
    }

    public void initSocket() {
        try {
            log.info("Init BR and OSW");
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.loadBalancerSocket.getInputStream()));
            this.outputStreamWriter = new OutputStreamWriter(this.loadBalancerSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void read() {
        this.executorService.execute(() -> {
            try {
                String in;
                log.info("Reading from inputStream...");
                while (!this.isStopped.get()) {
                    in = this.bufferedReader.readLine();
                    if (in == null) {
                        log.debug("Received null, closing socket.");
                        this.stop();
                    }
                    log.debug("Read: {}", in);
                    messageHandler.handleMessage(new Gson().fromJson(in, Message.class));
                }
            } catch (SocketTimeoutException e) {
                log.debug("No message was received for 30 seconds, closing connection...");
                this.stop();
            } catch (IOException e) {
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
            this.loadBalancerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Void call() throws Exception {
        initSocket();
        read();
        write(new Message<>(200, Message.MessageType.INFO, 2000));
        return null;
    }
}