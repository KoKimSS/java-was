package codesquad.was.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class UrlPathResourceMap {
    private static final Map<String, String> resourcePathMap = new HashMap<String, String>();

    private UrlPathResourceMap() {
    }

    public static UrlPathResourceMap factoryMethod() {
        return new UrlPathResourceMap();
    }

    public void setResourcePathMap(String path,String resourcePath) {
        resourcePathMap.put(path, resourcePath);
    }

    /**
     * 주어진 URL에 해당하는 리소스 경로를 반환합니다.
     * @param uri 요청된 URL
     * @return URL에 해당하는 리소스 경로
     */
    public static String getResourcePathByUrlPath(String uri) {

        // 자료를 요청하는 경우 자료 경로를 그대로 반환
        if (uri.endsWith(".svg")) {
            return "/static" + uri;
        } else if (uri.endsWith(".png")) {
            return "/static" + uri;
        } else if (uri.endsWith(".jpg") || uri.endsWith(".jpeg")) {
            return "/static" + uri;
        } else if (uri.endsWith(".css")) {
            return "/static" + uri;
        } else if (uri.endsWith(".js")) {
            return "/static" + uri;
        } else if (uri.endsWith(".ico")) {
            return "/static" + uri;
        }
        // URL 을 통한 자료요청은 매핑을 시켜 자료 반환
        return resourcePathMap.getOrDefault(uri, "/static"+uri);
    }
}
