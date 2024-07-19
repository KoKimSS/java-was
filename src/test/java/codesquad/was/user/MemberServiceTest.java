package codesquad.was.user;

import codesquad.business.domain.Member;
import codesquad.business.service.MemberService;
import codesquad.was.exception.BadRequestException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static codesquad.business.repository.MemberMemoryRepository.*;


class MemberServiceTest {

    private final MemberService memberService = new MemberService(userMemoryRepository);

    @AfterEach
    void tearDown() {
        userMemoryRepository.deleteAll();
    }

    @Test
    void save() {
        //given
        String id = "seung12";
        String name = "seungsu";
        String password = "994499";
        Member member = Member.factoryMethod(id, name, password);

        //when
        memberService.save(member);

        //then
        Assertions.assertThat(userMemoryRepository.getSize()).isEqualTo(1);

    }

    @Test
    void get() throws BadRequestException {
        //given
        String id = "seung123";
        String name = "seungsu";
        String password = "994499";
        Member member = Member.factoryMethod(id, name, password);

        //when
        Long saved = memberService.save(member);
        Member savedMember = memberService.get(saved);

        //then
        Assertions.assertThat(member).isEqualTo(savedMember);
    }
}