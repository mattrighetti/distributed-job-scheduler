package Client.Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketService {
    private String hostIp;
    private int servicePort;
    private Socket serviceSocket;
    private ObjectInputStream objectInputStream;
    private ObjectOutputStream objectOutputStream;

    public SocketService(String hostIp, String servicePort) {
        this.hostIp = hostIp;
        this.servicePort = Integer.parseInt(servicePort);
    }

    public void establishConnection() throws IOException {
        try {
            this.serviceSocket = new Socket(this.hostIp, this.servicePort);
            this.objectInputStream = new ObjectInputStream(this.serviceSocket.getInputStream());
            this.objectOutputStream = new ObjectOutputStream(this.serviceSocket.getOutputStream());
        } catch (UnknownHostException e) {
            System.out.println("Encountered error connecting to host.");
            e.printStackTrace();
        }
    }

    public void shutdownConnection() {
        try {
            this.objectOutputStream.close();
            this.objectInputStream.close();
            this.serviceSocket.close();
        } catch (IOException e) {
            System.out.println("Something went wrong during connection shutdown.");
            e.printStackTrace();
        }
    }

}
