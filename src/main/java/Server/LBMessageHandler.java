package Server;

import Server.LoadBalancer.NodeHandler;

public interface LBMessageHandler {
    <T> void handleMessage(Message<T> message, NodeHandler nodeHandler);
}
