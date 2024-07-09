package codesquad.was.handler;

import java.util.concurrent.ConcurrentHashMap;

public class HandlerMap {
    private final static ConcurrentHashMap<String, Handler> handlerMap = new ConcurrentHashMap<>() {{
        put("/create", SingUpHandler.singUpHandler);
        put("/login", LoginHandler.loginHandler);
    }};

    public static Handler getHandler(String urlPath) {
        return handlerMap.get(urlPath);
    }

    private HandlerMap() {
    }
}
