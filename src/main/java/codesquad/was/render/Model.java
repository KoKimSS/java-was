package codesquad.was.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Model {
    private final Map<String, Object> singleData;
    private final Map<String, List<Object>> listData;

    public Model() {
        this.listData = new HashMap<>();
        this.singleData = new HashMap<>();
    }

    public void addSingleData(String key, Object value) {
        singleData.put(key, value);
    }

    public void addListData(String key, List<Object> value) {
        listData.put(key, value);
    }

    public Map<String, Object> getSingleData() {
        return singleData;
    }

    public Object getSingleData(String key) {
        return singleData.get(key);
    }

    public Map<String, List<Object>> getListData() {
        return listData;
    }

    public List<Object> getListData(String key) {
        return listData.get(key);
    }

    public boolean containsKey(String attribute) {
        return singleData.containsKey(attribute);
    }

    public Object getAttribute(String attribute) {
        return singleData.get(attribute);
    }
}
