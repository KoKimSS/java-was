package codesquad.business.repository;

import codesquad.business.domain.Poster;
import codesquad.business.domain.User;

import java.util.concurrent.ConcurrentHashMap;

public class PosterRepository implements Repository<Long,Poster> {
    private static final ConcurrentHashMap<Long, Poster> map = new ConcurrentHashMap<>();
    public static final PosterRepository posterRepository = new PosterRepository();
    private PosterRepository() {}

    @Override
    public Poster findById(Long key) {
        return map.get(key);
    }

    @Override
    public void save(Long key, Poster value) {
        map.put(key, value);
    }

    @Override
    public void deleteById(Long key) {
        map.remove(key);
    }
}
