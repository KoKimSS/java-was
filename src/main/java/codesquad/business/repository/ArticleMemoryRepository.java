package codesquad.business.repository;

import codesquad.business.domain.Article;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ArticleMemoryRepository implements ArticleRepository {
    private static final ConcurrentHashMap<Long, Article> map = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1); // 초기값 설정

    public static final ArticleMemoryRepository ARTICLE_REPOSITORY = new ArticleMemoryRepository();
    private ArticleMemoryRepository() {}

    @Override
    public Article findById(Long key) {
        return map.get(key);
    }

    @Override
    public Long save(Article article) {
        long andIncrement = idGenerator.getAndIncrement();
        article.setId(andIncrement);
        map.put(andIncrement, article);
        return andIncrement;
    }

    @Override
    public void deleteById(Long key) {
        map.remove(key);
    }

    @Override
    public List<Article> findAll() {
        return map.values().stream().toList();
    }
}
