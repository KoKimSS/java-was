package codesquad.was.repository;


public interface Repository<K, V> {

    V findById(K key);

    void save(K key, V value);

    void deleteById(K key);
}
