package codesquad.http.repository;

import codesquad.http.log.Log;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryRepository {
    private static final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    public static void put(String key, Object value) {
        map.put(key, value);
        Log.log("key: " + key + " value: " + value + "저장");
    }

    public static Optional<Object> get(String key) {
        return Optional.of(map.get(key));
    }

    public static boolean containsKey(String key) {
        return map.containsKey(key);
    }
}
