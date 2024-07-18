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
        return headers.getOrDefault(header,null);
    }

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String toString() {
        return headers.toString();
    }
}
