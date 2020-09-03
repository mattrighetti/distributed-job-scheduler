package ds.loadbalancer;

import ds.common.Message;

public interface LBMessageHandler {
    <T> void handleMessage(Message<T> message, NodeHandler nodeHandler);
}
