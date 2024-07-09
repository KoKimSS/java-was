package codesquad.was.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    public static final ConcurrentHashMap<String, Object> sessions = new ConcurrentHashMap<>();

    private Session() {}

    public static Object get(String key) {
        return sessions.get(key);
    }

    public static void putSession(String sessionId, Object value) {
        sessions.put(sessionId, value);
    }

    public static String createSession() {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new ConcurrentHashMap<>());
        return sessionId;
    }
}
