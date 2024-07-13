package codesquad.business.repository;

import codesquad.business.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository implements Repository<String,User> {
    private static final ConcurrentHashMap<String, User> map = new ConcurrentHashMap<>();
    public static final UserRepository userRepository = new UserRepository();
    private UserRepository() {}

    public int getSize() {
        return map.size();
    }

    public boolean isExists(String key) {
        return map.containsKey(key);
    }

    public List<Object> getAllObject() {
        return new ArrayList<>(map.values());
    }

    public List<User> getAll() {
        return new ArrayList<>(map.values());
    }


    @Override
    public User findById(String key) {
        return map.get(key);
    }

    @Override
    public void save(String key, User value) {
        map.put(key, value);
    }

    @Override
    public void deleteById(String key) {
        map.remove(key);
    }
}
