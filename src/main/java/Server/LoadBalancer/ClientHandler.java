package Server.LoadBalancer;

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

public class ClientHandler implements Callable<Void> {
    private final Socket clientSocket;
    private ExecutorService executorService = Executors.newFixedThreadPool(2);

    private static final Logger log = LogManager.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void read() {
        this.executorService.execute(() -> {
            try (
                    BufferedReader inputStream = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()))
            ) {
                while (true) {
                    String inputJSON = inputStream.readLine();
                    log.debug("Received JSON: {}", inputJSON);
                }
            } catch (IOException e) {
                log.error("Encountered error while reading from inputStream of {}",
                        this.clientSocket.getInetAddress());
                e.printStackTrace();
            }
        });
    }

    public void write(String JSONString) {
        try (
                OutputStreamWriter outputStream = new OutputStreamWriter(this.clientSocket.getOutputStream())
        ) {
            outputStream.write(JSONString);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Void call() throws Exception {
        read();
        write("{ \"status\" : \"200\",  }");
        return null;
    }
}
