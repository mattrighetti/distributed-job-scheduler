package ds.common.Utils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamUtils {
    public static <T> List<T> emptyResultList(Map<T, Optional<T>> map) {
        return map.entrySet()
                .stream()
                .filter(pair -> pair.getValue().isEmpty())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
