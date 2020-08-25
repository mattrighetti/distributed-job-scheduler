package Server;

public interface MessageHandler {
    <T> void handleMessage(Message<T> message);
}
