package codesquad.was.render;

import java.util.HashMap;
import java.util.Map;

class Model {
    private final Map<String, String> data;

    public Model() {
        this.data = new HashMap<>();
    }

    public void addAttribute(String key, String value) {
        data.put(key, value);
    }

    public Map<String, String> getData() {
        return data;
    }
}
