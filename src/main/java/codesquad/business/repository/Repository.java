package codesquad.business.repository;


import java.sql.SQLException;

public interface Repository<K, V> {

    V findById(K key);

    K save(V value);

    void deleteById(K key);
}
