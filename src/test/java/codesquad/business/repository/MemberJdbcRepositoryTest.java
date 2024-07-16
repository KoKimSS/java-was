package codesquad.business.repository;

import codesquad.business.domain.Member;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemberJdbcRepositoryTest {

    private MemberJdbcRepository memberRepository = new MemberJdbcRepository();


    @Test
    public void testSaveAndFindById() {
        Member member = Member.factoryMethod("user1", "User One", "password123");
        Long userId = memberRepository.save(member);

        Member savedMember = memberRepository.findById(userId);

        assertNotNull(savedMember);
        assertEquals("user1", savedMember.getUserId());
        assertEquals("User One", savedMember.getUsername());
        assertEquals("password123", savedMember.getPassword());
    }

    @Test
    public void testDeleteById() {
        Member member = Member.factoryMethod("user2", "User Two", "password456");
        Long userId = memberRepository.save(member);

        memberRepository.deleteById(userId);

        Member deletedMember = memberRepository.findById(userId);

        assertNull(deletedMember);
    }

    @Test
    public void testIsExists() {
        Member member = Member.factoryMethod("user3", "User Three", "password789");
        memberRepository.save(member);

        assertTrue(memberRepository.isExists("user3"));
        assertFalse(memberRepository.isExists("nonexistent_user"));
    }
}