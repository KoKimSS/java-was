package codesquad.business.dao;
public class CommentDao {
    private Long id;
    private String contents;
    private Long userId;
    private String userName;
    private Long posterId;


    public CommentDao(Long id, String contents, Long userId, String userName, Long posterId) {
        this.id = id;
        this.contents = contents;
        this.userId = userId;
        this.userName = userName;
        this.posterId = posterId;
    }

    public Long getId() {
        return id;
    }

    public String getContents() {
        return contents;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Long getPosterId() {
        return posterId;
    }
}
