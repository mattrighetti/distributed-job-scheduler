package Server.Cluster;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoadBalancerHandler implements Callable<Void> {
    private final Socket loadBalancerSocket;
    private BufferedReader bufferedReader;
    private OutputStreamWriter outputStreamWriter;
    private final AtomicBoolean isStopped;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private static final Logger log = LogManager.getLogger(LoadBalancerHandler.class.getName());

    public LoadBalancerHandler(Socket loadBalancerSocket) {
        this.loadBalancerSocket = loadBalancerSocket;
        this.isStopped = new AtomicBoolean(false);
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
                        throw new IOException();
                    }
                    log.debug("Read: {}", in);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void write(String JSONString) {
        try {
            log.info("Writing to outputStream");
            this.outputStreamWriter.write(JSONString);
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
        new Thread(() -> {
            write("{ \"status\" : \"300\"}\n");
        }).start();
        return null;
    }
}
