package ds.common;

public class Message<T> {
    public enum MessageType {
        JOB,
        INFO,
        RESULT
    }

    public final int status;
    public final MessageType messageType;
    public final T payload;

    public Message(int status, MessageType messageType, T payload) {
        this.status = status;
        this.messageType = messageType;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "status=" + status +
                ", messageType=" + messageType +
                ", payload=" + payload +
                '}';
    }
}
