package codesquad.business.domain;

import org.h2.engine.User;

public class Comment {
    private Long id;
    private String contents;
    private Long userId;
    private Long posterId;

    public Comment(Long id, String contents, Long userId, Long posterId) {
        this.id = id;
        this.contents = contents;
        this.userId = userId;
        this.posterId = posterId;
    }
}
