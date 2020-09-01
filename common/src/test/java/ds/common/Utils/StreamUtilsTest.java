package ds.common.Utils;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamUtilsTest {

    @Test
    public void emptyResultsTestOne() {
        Map<String, Optional<String>> map = new HashMap<>();
        map.put("one", Optional.empty());
        map.put("two", Optional.empty());
        map.put("three", Optional.empty());
        map.put("four", Optional.of("test"));
        map.put("five", Optional.empty());
        map.put("six", Optional.empty());

        List<String> emptyVal = StreamUtils.emptyResultList(map);

        List<String> result = new ArrayList<>();
        result.add("one");
        result.add("two");
        result.add("three");
        result.add("five");
        result.add("six");

        assertEquals(emptyVal.size(), 5);
        assertEquals(result.size(), 5);
        assertTrue(emptyVal.containsAll(result));
    }

    @Test
    public void emptyResultsTestTwo() {
        Map<String, Optional<String>> map = new HashMap<>();
        map.put("one", Optional.of("test"));
        map.put("two", Optional.of("test"));
        map.put("three", Optional.of("test"));
        map.put("four", Optional.of("test"));
        map.put("five", Optional.of("test"));
        map.put("six", Optional.of("test"));

        List<String> emptyVal = StreamUtils.emptyResultList(map);

        List<String> result = new ArrayList<>();

        assertTrue(emptyVal.containsAll(result));
        assertTrue(emptyVal.isEmpty());
        assertTrue(result.isEmpty());
    }

}
