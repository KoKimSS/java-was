package codesquad.business.domain;

public class Article {
    private Long id;
    private String title;
    private String contents;
    private Long userId;

    private Article(Long id,String title, String contents, Long userId) {
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

    public static Article FactoryMethod(String title, String contents, Long userId) {
        return new Article(null,title, contents, userId);
    }
}
