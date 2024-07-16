package codesquad.business.repository;

import codesquad.business.domain.Member;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MemberMemoryRepository implements MemberRepository {
    private static final ConcurrentHashMap<Long, Member> map = new ConcurrentHashMap<>();
    public static final MemberMemoryRepository userMemoryRepository = new MemberMemoryRepository();
    private static final AtomicLong idGenerator = new AtomicLong(1); // 초기값 설정

    private MemberMemoryRepository() {}

    public int getSize() {
        return map.size();
    }

    public boolean isExists(Long key) {
        return map.containsKey(key);
    }

    public boolean isExists(String userId) {
        return map.values().stream()
                        .anyMatch(user -> user.getUserId().equals(userId));
    }

    public List<Object> getAllObject() {
        return new ArrayList<>(map.values());
    }

    public List<Member> getAll() {
        return new ArrayList<>(map.values());
    }

    @Override
    public Member findById(Long key) {
        return map.get(key);
    }

    @Override
    public Long save(Member value) {
        long andIncrement = idGenerator.getAndIncrement();
        map.put(andIncrement, value);
        return andIncrement;
    }

    @Override
    public void deleteById(Long key) {

    }
}
