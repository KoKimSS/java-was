package codesquad.business.repository;

import codesquad.business.domain.Comment;

import java.util.List;

public interface CommentRepository extends Repository<Long, Comment> {
    List<Comment> findAllByArticleId(Long articleId);
}
