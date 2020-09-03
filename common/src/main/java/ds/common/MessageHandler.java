package ds.common;

public interface MessageHandler {
    <T> void handleMessage(Message<T> message);
}
