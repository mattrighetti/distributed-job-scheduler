package ds.common;

public interface Storable<T> {
    T readFromFile();

    void saveToFile();
}
