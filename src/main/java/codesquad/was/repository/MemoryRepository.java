package codesquad.was.repository;

import codesquad.was.log.Log;
import codesquad.was.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryRepository {
    private static final ConcurrentHashMap<String, Object> map = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MemoryRepository.class);

    private MemoryRepository() {}

    public static void put(String key, Object value) {
        map.put(key, value);
        logger.info("key: {} value: {}저장", key, value);
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
        map.forEach((k, v) -> logger.info("key: {} value: {}", k, v));
    }
}
