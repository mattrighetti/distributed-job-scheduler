package ds.loadbalancer;

import ds.loadbalancer.Dispatcher;
import ds.common.Utils.Tuple2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;

public class DispatcherTest {

    @Test
    public void convertTest() {
        Map<String, Integer> map = new HashMap<>();
        map.put("first", 0);
        map.put("second", 1);
        map.put("third", 2);
        List<Tuple2<Integer, String>> list = Dispatcher.convertNodesInfoToList(map);
        assertEquals(list.size(), 3);
        assertEquals(list.get(0), new Tuple2<>(0, "first"));
        assertEquals(list.get(1), new Tuple2<>(1, "second"));
        assertEquals(list.get(2), new Tuple2<>(2, "third"));
    }

    @Test
    public void applyAlgorithmTestOne() {
        Map<String, Integer> map = new HashMap<>();
        map.put("first", 0);
        map.put("second", 1);
        map.put("third", 5);
        List<Tuple2<Integer, String>> list = Dispatcher.convertNodesInfoToList(map);

        IntConsumer ic = (value) -> {
            while (value > 0) {
                list.get(0).item1 += 1;
                list.sort(Comparator.comparingInt(o -> o.item1));
                value--;
            }
        };

        Dispatcher.applyAlgorithmFunction(9, ic);

        assertEquals(list.get(0).item1, list.get(1).item1);
        assertEquals(list.get(1).item1, list.get(2).item1);
        assertEquals(list.get(0).item1, list.get(2).item1);
    }

    @Test
    public void applyAlgorithmTestTwo() {
        Map<String, Integer> map = new HashMap<>();
        map.put("first", 0);
        map.put("second", 0);
        map.put("third", 0);
        List<Tuple2<Integer, String>> list = Dispatcher.convertNodesInfoToList(map);

        IntConsumer ic = (value) -> {
            while (value > 0) {
                list.get(0).item1 += 1;
                list.sort(Comparator.comparingInt(o -> o.item1));
                value--;
            }
        };

        Dispatcher.applyAlgorithmFunction(3, ic);
        assertEquals(list.get(0).item1, 1);
        assertEquals(list.get(0).item1, list.get(1).item1);
        assertEquals(list.get(1).item1, list.get(2).item1);
        assertEquals(list.get(0).item1, list.get(2).item1);
    }

    @Test
    public void applyAlgorithmTestThree() {
        Map<String, Integer> map = new HashMap<>();
        map.put("first", 11);
        map.put("second", 0);
        map.put("third", 0);
        List<Tuple2<Integer, String>> list = Dispatcher.convertNodesInfoToList(map);

        IntConsumer ic = (value) -> {
            while (value > 0) {
                list.get(0).item1 += 1;
                list.sort(Comparator.comparingInt(o -> o.item1));
                value--;
            }
        };

        Dispatcher.applyAlgorithmFunction(10, ic);
        assertEquals(list.get(2).item1, 11);
        assertEquals(list.get(0).item1, list.get(1).item1);

        Dispatcher.applyAlgorithmFunction(3, ic);
        assertEquals(list.get(2).item1, 11);
        assertTrue((list.get(0).item1 == 6 && list.get(1).item1 == 7) ||
                (list.get(0).item1 == 7 && list.get(1).item1 == 6));
    }

}
