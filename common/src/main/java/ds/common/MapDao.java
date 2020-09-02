package ds.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MapDao<K, V> implements Storable<Map<K, V>> {
    private final Map<K, V> map;
    private final String filename;

    public static final Logger log = LogManager.getLogger(MapDao.class.getName());

    public MapDao(String filename) {
        this.filename = filename;
        this.map = readFromFile();
    }

    @Override
    public Map<K, V> readFromFile() {
        Optional<Map<K, V>> map = FileStorage.readObjFromFile(filename, true);
        return map.orElseGet(ConcurrentHashMap::new);
    }

    public Map<K, V> getMap() {
        return this.map;
    }

    public V get(K key) {
        return this.map.get(key);
    }

    public void put(K key, V value) {
        this.map.put(key, value);
        saveToFile();
    }

    @Override
    public void saveToFile() {
        FileStorage.writeObjToFile(this.map, filename, true);
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean containsKey(K key) {
        return this.map.containsKey(key);
    }
}
