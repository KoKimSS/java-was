package codesquad.business.domain;

public class Article {
    private Long id;
    private String title;
    private String contents;
    private Long userId;
    private String filePath;

    public Article(Long id, String title, String contents, Long userId, String filePath) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.userId = userId;
        this.filePath = filePath;
    }

    private Article(Long id, String title, String contents, Long userId) {
        this.title = title;
        this.id = id;
        this.contents = contents;
        this.userId = userId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFilePath() {
        return filePath;
    }

    public static Article FactoryMethod(String title, String contents, Long userId) {
        return new Article(null,title, contents, userId);
    }
    public static Article FactoryMethod(String title, String contents, Long userId,String filePath) {
        return new Article(null,title, contents, userId,filePath);
    }
    public static Article FactoryMethod(Long id,String title, String contents, Long userId) {
        return new Article(id,title, contents, userId);
    }

    public static Article FactoryMethod(Long id,String title, String contents, Long userId,String filePath) {
        return new Article(id,title, contents, userId,filePath);
    }
}
