package codesquad.business.repository;

import codesquad.business.domain.Article;

import java.util.List;

public interface ArticleRepository extends Repository<Long, Article> {
    List<Article> findAll();
}
