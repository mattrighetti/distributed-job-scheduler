package ds.common.Utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamUtils {

    public static <T> List<T> emptyResultList(Map<T, T> map) {
        return map.entrySet()
                .stream()
                .filter(pair -> pair.getValue() == null)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static <T> List<Tuple2<String, T>> availableResults(List<String> requested,
                                                                    Map<String, T> resultsMap) {
        return requested.stream()
                .flatMap(s ->
                        resultsMap.containsKey(s) ? Stream.of(new Tuple2<>(s, resultsMap.get(s))) : Stream.empty()
                )
                .filter(tuple -> tuple.item2 != null)
                .map(tuple -> new Tuple2<>(tuple.item1, tuple.item2))
                .collect(Collectors.toList());
    }
}
