package codesquad.http.urlMapper;

import java.util.HashMap;
import java.util.Map;

public class UrlResourceMap {
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

    private UrlResourceMap() {
    }

    /**
     * 주어진 URL에 해당하는 리소스 경로를 반환합니다.
     *
     * @param url 요청된 URL
     * @return URL에 해당하는 리소스 경로
     */
    public static String getResourcePathByUrl(String url) {

        // 자료를 요청하는 경우 자료 경로를 그대로 반환
        if (url.endsWith(".svg")) {
            return "/static" + url;
        } else if (url.endsWith(".png")) {
            return "/static" + url;
        } else if (url.endsWith(".jpg") || url.endsWith(".jpeg")) {
            return "/static" + url;
        } else if (url.endsWith(".css")) {
            return "/static" + url;
        } else if (url.endsWith(".js")) {
            return "/static" + url;
        } else if (url.endsWith(".ico")) {
            return "/static" + url;
        }

        // URL 을 통한 자료요청은 매핑을 시켜 자료 반환
        return resourcePathMap.get(url);
    }
}