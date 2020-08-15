package Server.Network.Service;

public interface ServerService<T> {
    void handle(T t);
}
