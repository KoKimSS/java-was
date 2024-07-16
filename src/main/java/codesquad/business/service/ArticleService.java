package codesquad.business.service;

import codesquad.business.domain.Article;
import codesquad.business.repository.ArticleRepository;

public class ArticleService {

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    public long saveArticle(Article article) {
        return articleRepository.save(article);
    }
}
