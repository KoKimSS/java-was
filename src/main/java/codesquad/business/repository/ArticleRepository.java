package codesquad.business.repository;

import codesquad.business.dao.ArticleDao;
import codesquad.business.domain.Article;

import java.util.List;

public interface ArticleRepository extends Repository<Long, Article> {
    List<Article> findAll();

    ArticleDao getArticleDao(Long key);
}
