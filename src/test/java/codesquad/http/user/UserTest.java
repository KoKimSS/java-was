package codesquad.http.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    @DisplayName("올바른 값으로 유저객체를 생성한다.")
    void factoryMethod() {
        final String userId = "seungsu";
        final String username = "김승수";
        final String password = "123123";

        User user = User.factoryMethod(userId, username, password);
        assertEquals(userId, user.getUserId());
        assertEquals(username, user.getUsername());
        assertEquals(password, user.getPassword());
    }


}