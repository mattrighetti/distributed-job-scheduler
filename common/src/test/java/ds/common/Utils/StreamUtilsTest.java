package ds.common.Utils;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static ds.common.Utils.Strings.NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamUtilsTest {

    @Test
    public void emptyResultsTestOne() {
        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("one", NULL.toString());
        map.put("two", NULL.toString());
        map.put("three", NULL.toString());
        map.put("four", "test");
        map.put("five", NULL.toString());
        map.put("six", NULL.toString());

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
        Map<String, String> map = new HashMap<>();
        map.put("one", "test");
        map.put("two", "test");
        map.put("three", "test");
        map.put("four", "test");
        map.put("five", "test");
        map.put("six", "test");

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

        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("one", "test");
        map.put("two", "test");
        map.put("three", "test");
        map.put("four", "test");
        map.put("five", "test");
        map.put("six", "test");

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

        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("one", NULL.toString());
        map.put("two", "test");
        map.put("three", "test");
        map.put("four", "test");
        map.put("five", "test");
        map.put("six", "test");

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

        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("one", NULL.toString());
        map.put("two", NULL.toString());
        map.put("three", "test");
        map.put("four", "test");
        map.put("five", "test");
        map.put("six", "test");

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

        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("five", NULL.toString());
        map.put("D", NULL.toString());

        List<Tuple2<String, String>> result = StreamUtils.availableResults(requested, map);
        List<Tuple2<String, String>> expected = new ArrayList<>();

        assertTrue(result.isEmpty());
        assertTrue(result.containsAll(expected));
        assertTrue(expected.containsAll(result));
    }

}
