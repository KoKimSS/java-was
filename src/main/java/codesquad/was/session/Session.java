package codesquad.was.session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Session {
    public static final String sessionStr = "sessionId";

    public final ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<>();

    public Session() {}

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public static String createSessionId() {
        return UUID.randomUUID().toString();
    }
}
