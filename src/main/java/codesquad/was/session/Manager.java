package codesquad.was.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Manager {
    protected static Map<String,Session> sessions = new ConcurrentHashMap<>();

    private Manager() {}

    public static Session findSession(String sessionId) {
        return sessions.get(sessionId);
    }
    public static void addSession(String sessionId,Session session) {
        sessions.put(sessionId, session);
    }
    public static void removeSession(String sessionId) {
        sessions.remove(sessionId);
    }
}
