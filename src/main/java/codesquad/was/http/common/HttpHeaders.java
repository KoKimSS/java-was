package codesquad.was.http.common;

import java.util.*;

public class HttpHeaders {
    private final Map<String, List<String>> headers = new HashMap<>();


    public HttpHeaders() {
    }

    public void addHeader(String header, String value) {
        List<String> values = headers.computeIfAbsent(header, k -> new ArrayList<>());
        values.add(value);
    }
    public List<String> getHeader(String header) {
        return headers.getOrDefault(header, null);
    }

    public String getValue(String header) {
        List<String> values = headers.get(header);
        return (values != null && !values.isEmpty()) ? values.get(0) : null;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    // HttpResponseSender에서 사용하는 단순한 헤더 맵 반환
    public Map<String, String> getHeadersAsMap() {
        Map<String, String> simpleHeaders = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            List<String> values = entry.getValue();
            if (values != null && !values.isEmpty()) {
                simpleHeaders.put(entry.getKey(), values.get(0));
            }
        }
        return simpleHeaders;
    }

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String toString() {
        return headers.toString();
    }
}
