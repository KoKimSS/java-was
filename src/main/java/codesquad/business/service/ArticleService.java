package codesquad.business.service;

import codesquad.business.domain.Article;
import codesquad.business.repository.ArticleJdbcRepository;
import codesquad.business.repository.ArticleRepository;

import java.util.List;

public class ArticleService {

    private final ArticleRepository articleRepository;

    public static ArticleService articleService = new ArticleService(ArticleJdbcRepository.articleJdbcRepository);

    public ArticleService(ArticleRepository articleMemoryRepository) {
        this.articleRepository = articleMemoryRepository;
    }

    public long saveArticle(Article article) {
        return articleRepository.save(article);
    }

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article findById(Long id) {
        return articleRepository.findById(id);
    }
}
