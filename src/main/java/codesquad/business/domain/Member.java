package codesquad.business.domain;

import java.util.Objects;

public class Member {
    private Long id;
    private String userId;
    private String username;
    private String password;

    private Member(String userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }
    private Member(Long id, String userId, String username, String password) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member member)) return false;
        return Objects.equals(userId, member.userId) && Objects.equals(username, member.username) && Objects.equals(password, member.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password);
    }

    public Long getId() {
        return id;
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

    public static Member factoryMethod(Long id, String userId, String username, String password) {
        return new Member(id,userId, username, password);
    }

    public static Member factoryMethod(String userId, String username, String password) {
        return new Member(userId, username, password);
    }
}
