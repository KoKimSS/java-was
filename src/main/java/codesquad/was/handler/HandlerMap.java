package codesquad.was.handler;

import codesquad.was.user.UserRepository;

import java.util.concurrent.ConcurrentHashMap;

public class HandlerMap {
    private final static ConcurrentHashMap<String, Handler> handlerMap = new ConcurrentHashMap<>() {{
        put("GET /create", (Handler) new UserHandler(new UserRepository()));
    }};

    public static Handler getHandler(String method,String urlPath) {
        return handlerMap.get(method+" "+urlPath);
    }

    private HandlerMap() {
    }
}
