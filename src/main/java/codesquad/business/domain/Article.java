package codesquad.business.domain;

public class Article {
    private Long id;
    private String contents;
    private String userId;

    private Article(Long id, String contents, String userId) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static Article FactoryMethod(String contents, String userId) {
        return new Article(null, contents, userId);
    }
}
