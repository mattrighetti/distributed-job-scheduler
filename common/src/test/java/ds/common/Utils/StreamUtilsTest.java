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

    @Test
    public void availableResultTestOne() {
        List<String> requested = new ArrayList<>();
        requested.add("one");
        requested.add("two");

        Map<String, Optional<String>> map = new HashMap<>();
        map.put("one", Optional.of("test"));
        map.put("two", Optional.of("test"));
        map.put("three", Optional.of("test"));
        map.put("four", Optional.of("test"));
        map.put("five", Optional.of("test"));
        map.put("six", Optional.of("test"));

        List<Tuple2<String, String>> result = StreamUtils.availableResults(requested, map);
        List<Tuple2<String, String>> expected = new ArrayList<>();
        expected.add(new Tuple2<>("one", "test"));
        expected.add(new Tuple2<>("two", "test"));

        assertEquals(result.size(), 2);
        assertTrue(result.containsAll(expected));
        assertTrue(expected.containsAll(result));
    }

    @Test
    public void availableResultTestTwo() {
        List<String> requested = new ArrayList<>();
        requested.add("one");
        requested.add("two");

        Map<String, Optional<String>> map = new HashMap<>();
        map.put("one", Optional.empty());
        map.put("two", Optional.of("test"));
        map.put("three", Optional.of("test"));
        map.put("four", Optional.of("test"));
        map.put("five", Optional.of("test"));
        map.put("six", Optional.of("test"));

        List<Tuple2<String, String>> result = StreamUtils.availableResults(requested, map);
        List<Tuple2<String, String>> expected = new ArrayList<>();
        expected.add(new Tuple2<>("two", "test"));

        assertEquals(result.size(), 1);
        assertTrue(result.containsAll(expected));
        assertTrue(expected.containsAll(result));
    }

    @Test
    public void availableResultTestThree() {
        List<String> requested = new ArrayList<>();
        requested.add("one");
        requested.add("two");

        Map<String, Optional<String>> map = new HashMap<>();
        map.put("one", Optional.empty());
        map.put("two", Optional.empty());
        map.put("three", Optional.of("test"));
        map.put("four", Optional.of("test"));
        map.put("five", Optional.of("test"));
        map.put("six", Optional.of("test"));

        List<Tuple2<String, String>> result = StreamUtils.availableResults(requested, map);
        List<Tuple2<String, String>> expected = new ArrayList<>();

        assertTrue(result.isEmpty());
        assertTrue(result.containsAll(expected));
        assertTrue(expected.containsAll(result));
    }

    @Test
    public void availableResultTestFour() {
        List<String> requested = new ArrayList<>();
        requested.add("one");
        requested.add("two");

        Map<String, Optional<String>> map = new HashMap<>();
        map.put("five", Optional.empty());
        map.put("D", Optional.empty());

        List<Tuple2<String, String>> result = StreamUtils.availableResults(requested, map);
        List<Tuple2<String, String>> expected = new ArrayList<>();

        assertTrue(result.isEmpty());
        assertTrue(result.containsAll(expected));
        assertTrue(expected.containsAll(result));
    }

}
