package codesquad.business.dao;

public class ArticleDao {
    private Long id;
    private String title;
    private String contents;
    private Long userId;
    private String userName;
    private String filePath;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
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

    public String getFilePath() {
        return filePath;
    }

    public ArticleDao(Long id, String title, String contents, Long userId, String userName, String filePath) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.userId = userId;
        this.userName = userName;
        this.filePath = filePath;
    }
}
