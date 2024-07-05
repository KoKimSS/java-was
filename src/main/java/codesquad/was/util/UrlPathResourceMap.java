package codesquad.was.util;

import java.util.HashMap;
import java.util.Map;

public class UrlPathResourceMap {
    private static final Map<String, String> resourcePathMap = new HashMap<String, String>() {{
        // 간단한 예제 URL 패턴과 리소스 매핑 설정
        put("/", "/static/index.html");
        put("/index.html", "/static/index.html");
        put("/about", "about.html");
        put("/contact", "contact.html");
        put("/css/styles.css", "styles.css");
        put("/js/main.js", "main.js");
        put("/registration", "/static/registration/index.html");
    }};

    private UrlPathResourceMap() {
    }

    /**
     * 주어진 URL에 해당하는 리소스 경로를 반환합니다.
     *
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
        return resourcePathMap.get(uri);
    }
}
