package codesquad.was.handler;

import codesquad.was.user.UserRepository;

import java.util.concurrent.ConcurrentHashMap;

public class HandlerMap {
    private final static ConcurrentHashMap<String, Handler> handlerMap = new ConcurrentHashMap<>() {{
        put("/create", new UserHandler(new UserRepository()));
    }};

    public static Handler getHandler(String urlPath) {
        return handlerMap.get(urlPath);
    }

    private HandlerMap() {
    }
}
