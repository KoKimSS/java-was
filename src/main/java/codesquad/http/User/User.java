package codesquad.http.User;

public class User {
    private String userId;
    private String username;
    private String password;

    private User(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public static User factoryMethod(String id, String username, String password) {
        return new User(id, username, password);
    }
}
