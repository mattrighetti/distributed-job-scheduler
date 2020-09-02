package ds.common;

import java.util.Deque;

public interface Dao<T> {
    Deque<T> get();

    T removeFirst();

    void add(T t);

    int size();

    boolean isEmpty();

    void addLast(Job job);
}
