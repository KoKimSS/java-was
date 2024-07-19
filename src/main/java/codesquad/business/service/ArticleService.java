package codesquad.business.service;

import codesquad.business.dao.ArticleDao;
import codesquad.business.domain.Article;
import codesquad.business.repository.ArticleJdbcRepository;
import codesquad.business.repository.ArticleRepository;

import java.util.List;

public class ArticleService {

    public static ArticleService articleService = new ArticleService(ArticleJdbcRepository.articleJdbcRepository);
    private final ArticleRepository articleRepository;

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

    public ArticleDao findDaoById(Long id) {
        return articleRepository.getArticleDao(id);
    }
}
