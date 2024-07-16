package codesquad.was.user;

import codesquad.business.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberTest {

    @Test
    @DisplayName("올바른 값으로 유저객체를 생성한다.")
    void factoryMethod() {
        final String userId = "seungsu";
        final String username = "김승수";
        final String password = "123123";

        Member member = Member.factoryMethod(userId, username, password);
        assertEquals(userId, member.getUserId());
        assertEquals(username, member.getUsername());
        assertEquals(password, member.getPassword());
    }


}