package codesquad.was.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public String toString() {
        return headers.toString();
    }
}
