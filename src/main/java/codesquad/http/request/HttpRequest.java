package codesquad.http.request;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private URL url;
    private String method;
    private String urlPath;
    private String version;
    private Map<String, String> headers;
    private Map<String, String> parameters;
    private String body;

    public HttpRequest() {
        headers = new HashMap<>();
        parameters = new HashMap<>();
    }

    // Getters and Setters
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
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
    private void parseParameters(String query) {
        if(query == null || query.isEmpty()) {
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
                "method='" + method + '\'' +
                ", url='" + url + '\'' +
                ", version='" + version + '\'' +
                ", headers=" + headers +
                ", parameters=" + parameters +
                ", body='" + body + '\'' +
                '}';
    }
}
