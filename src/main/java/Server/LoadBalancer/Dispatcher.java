package Server.LoadBalancer;

import Utils.Tuple2;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;

public class Dispatcher {

    public static <T> List<Tuple2<Integer, T>> convertNodesInfoToList(Map<T, Integer> integerMap) {
        Map<T, Integer> infoSnapshot = new HashMap<>(integerMap);
        return infoSnapshot
                .entrySet()
                .stream()
                .map(x -> new Tuple2<>(x.getValue(), x.getKey()))
                .sorted(Comparator.comparingInt(o -> o.item1))
                .collect(Collectors.toList());
    }

    public static void applyAlgorithmFunction(int value, IntConsumer lambda) {
        lambda.accept(value);
    }

}
