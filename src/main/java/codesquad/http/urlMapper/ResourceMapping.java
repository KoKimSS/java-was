package codesquad.http.urlMapper;

import java.util.HashMap;
import java.util.Map;

public class ResourceMapping {
    private Map<String, String> resourceMap;

    public ResourceMapping() {
        this.resourceMap = new HashMap<>();
        initializeMappings();
    }

    private void initializeMappings() {
        // 간단한 예제 URL 패턴과 리소스 매핑 설정
        resourceMap.put("/", "/static/index.html");
        resourceMap.put("/index.html", "/static/index.html");
        resourceMap.put("/about", "about.html");
        resourceMap.put("/contact", "contact.html");
        resourceMap.put("/css/styles.css", "styles.css");
        resourceMap.put("/js/main.js", "main.js");
        resourceMap.put("/registration", "/static/registration/index.html");
    }

    /**
     * 주어진 URL에 해당하는 리소스 경로를 반환합니다.
     *
     * @param url 요청된 URL
     * @return URL에 해당하는 리소스 경로
     */
    public String getResourcePath(String url) {
        if (url.startsWith("/img")) {
            return "/static" + url;
        }

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

        return resourceMap.get(url);
    }


    /**
     * 리소스 매핑을 업데이트하거나 새로운 매핑을 추가합니다.
     *
     * @param url        URL
     * @param resourcePath 해당 URL에 매핑될 리소스 경로
     */
    public void addResourceMapping(String url, String resourcePath) {
        resourceMap.put(url, resourcePath);
    }

    /**
     * 주어진 URL에 해당하는 리소스 매핑을 삭제합니다.
     *
     * @param url 삭제할 URL
     */
    public void removeResourceMapping(String url) {
        resourceMap.remove(url);
    }

    /**
     * 현재 설정된 모든 리소스 매핑을 반환합니다.
     *
     * @return 모든 리소스 매핑
     */
    public Map<String, String> getAllMappings() {
        return new HashMap<>(resourceMap);
    }

}
