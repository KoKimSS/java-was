package codesquad.business.service;

import codesquad.business.domain.Article;
import codesquad.business.repository.ArticleRepository;

public class ArticleService {

    public long saveArticle(Article article) {
        return ArticleRepository.ARTICLE_REPOSITORY.save(article);
    }
}
