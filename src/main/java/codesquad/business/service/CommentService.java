package codesquad.business.service;

import codesquad.business.domain.Comment;
import codesquad.business.repository.CommentJdbcRepository;
import codesquad.business.repository.CommentRepository;

import java.util.List;

public class CommentService {
    private final CommentRepository commentRepository;
    public static CommentService commentService = new CommentService(CommentJdbcRepository.commentJdbcRepository);

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public List<Comment> getListByArticleId(Long articleId) {
        List<Comment> comments = commentRepository.findAllByArticleId(articleId);
        return comments;
    }
}
