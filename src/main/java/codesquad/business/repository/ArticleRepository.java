package codesquad.business.repository;

import codesquad.business.domain.Article;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ArticleRepository implements Repository<Long, Article> {
    private static final ConcurrentHashMap<Long, Article> map = new ConcurrentHashMap<>();
    private static final AtomicLong idGenerator = new AtomicLong(1); // 초기값 설정

    public static final ArticleRepository ARTICLE_REPOSITORY = new ArticleRepository();
    private ArticleRepository() {}

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
}
