package ds.cluster;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private Scanner scanner;
    private PrintWriter printWriter;
    private final ClientSubmissionHandler clientSubmissionHandler;

    private static final Logger log = LogManager.getLogger(ClientHandler.class.getName());

    public ClientHandler(Socket socket, ClientSubmissionHandler clientSubmissionHandler) {
        this.socket = socket;
        this.clientSubmissionHandler = clientSubmissionHandler;
    }

    void initSocket() {
        try {
            this.scanner = new Scanner(this.socket.getInputStream(), UTF_8);
            this.printWriter = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream(), UTF_8), false);
        } catch (IOException e) {
            log.error("Encountered error while creating In-OutStreams to client.");
            e.printStackTrace();
        }
    }

    void askRoutine() {
        printWriter.println("Please submit the number of ms you would like to submit or write 'exit' to quit");
        printWriter.flush();
        boolean isDone = false;
        String line;
        while (!isDone) {
            printWriter.print("Milliseconds: ");
            printWriter.flush();
            line = scanner.nextLine();
            log.debug("Client entered '{}'", line);

            if (line.toLowerCase().trim().equals("exit")) {
                isDone = true;
            } else {
                try {
                    String ticketHash = clientSubmissionHandler.handleJobSubmission(Integer.parseInt(line));
                    printWriter.println("Ticket ID: " + ticketHash);
                    printWriter.flush();
                } catch (NumberFormatException e) {
                    log.error("Could not parse to integer.");
                    printWriter.println("Please enter a valid number.");
                }
            }
        }

        printWriter.println("Come back later to check your results.");
        printWriter.flush();

        this.printWriter.close();
        this.scanner.close();

        try {
            this.socket.close();
        } catch (IOException e) {
            log.error("Encountered error while closing clientSocket");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        initSocket();
        askRoutine();
    }
}
