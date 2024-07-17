package codesquad.was.handler;

import java.util.concurrent.ConcurrentHashMap;

public class HandlerMap {
    private final static ConcurrentHashMap<String, Handler> handlerMap = new ConcurrentHashMap<>();

    private HandlerMap() {
    }

    public static HandlerMap factoryMethod() {
        return new HandlerMap();
    }

    public void setHandlerMap(String path, Handler handler) {
        handlerMap.put(path, handler);
    }

    public Handler getHandler(String urlPath) {
        return handlerMap.get(urlPath);
    }
}
