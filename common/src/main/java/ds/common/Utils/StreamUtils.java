package ds.common.Utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ds.common.Utils.Strings.NULL;

public class StreamUtils {

    public static List<String> emptyResultList(Map<String, String> map) {
        return map.entrySet()
                .stream()
                .filter(pair -> pair.getValue().equals(NULL.toString()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public static List<Tuple2<String, String>> availableResults(List<String> requested,
                                                                    Map<String, String> resultsMap) {
        return requested.stream()
                .flatMap(s ->
                        resultsMap.containsKey(s) ? Stream.of(new Tuple2<>(s, resultsMap.get(s))) : Stream.empty()
                )
                .filter(tuple -> !tuple.item2.equals(NULL.toString()))
                .collect(Collectors.toList());
    }
}
