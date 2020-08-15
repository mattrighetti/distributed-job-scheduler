package Server.Network.Services;

import java.net.Socket;

public abstract class SocketRunnable implements Runnable {
    protected Socket socket;

    public SocketRunnable() {
        this.socket = null;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}
