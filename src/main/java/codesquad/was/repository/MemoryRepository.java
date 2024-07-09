package codesquad.was.repository;

import codesquad.was.log.Log;
import codesquad.was.user.User;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryRepository {
    private static final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();

    private MemoryRepository() {}

    public static void put(String key, Object value) {
        map.put(key, value);
        Log.log("key: " + key + " value: " + value + "저장");
    }

    public static Object get(String key) {
        return map.get(key);
    }

    public static boolean containsKey(String key) {
        return map.containsKey(key);
    }

    public static int getSize() {
        return map.size();
    }

    public static void printMap() {
        map.forEach((k, v) -> Log.log("key: " + k + " value: " + v));
    }
}
