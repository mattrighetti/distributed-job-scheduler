package ds.cluster;

import ds.common.Message;

public interface MessageHandler {
    <T> void handleMessage(Message<T> message);

    void handleReverseProxyDisconnection();
}
