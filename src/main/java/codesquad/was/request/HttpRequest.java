package codesquad.was.request;

import codesquad.was.common.HttpHeaders;
import codesquad.was.common.HttpMethod;
import codesquad.was.session.Session;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private URL url;
    private HttpMethod method;
    private String urlPath;
    private String version;
    private final HttpHeaders headers = new HttpHeaders();
    private final Map<String, String> parameters;
    private String contentType;
    private String body;
    //Cookie name ,value
    private final Map<String, String> cookies = new HashMap<>();
    private Session session;

    public HttpRequest() {
        parameters = new HashMap<>();
        session = new Session();
    }

    // Getters and Setters
    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = HttpMethod.getHttpMethodByString(method);
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
        this.urlPath = url.getPath();
        parseParameters(url.getQuery());
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void addHeader(String key, String value) {
        headers.addHeader(key, value);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(String key) {
        return parameters.get(key);
    }

    public String getUrlPath() {
        return urlPath;
    }

    // Parse parameters from URL
    public void parseParameters(String query) {
        if (query == null || query.isEmpty()) {
            return;
        }

        String[] paramPairs = query.split("&");
        for (String pair : paramPairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length > 1) {
                parameters.put(keyValue[0], keyValue[1]);
            } else {
                parameters.put(keyValue[0], "");
            }
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "url=" + url +
                ", method=" + method +
                ", urlPath='" + urlPath + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                ", parameters=" + parameters +
                ", contentType='" + contentType + '\'' +
                ", body='" + body + '\'' +
                ", cookies=" + cookies +
                ", session=" + session +
                '}';
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void addCookie(String key, String value) {
        cookies.put(key, value);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void addSession(Session session) {
        this.session = session;
    }
}
