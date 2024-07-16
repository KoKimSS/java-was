package codesquad.business.service;

import codesquad.business.domain.Member;
import codesquad.business.repository.MemberJdbcRepository;
import codesquad.business.repository.MemberMemoryRepository;
import codesquad.business.repository.MemberRepository;
import codesquad.was.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MemberService {
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;
    public static MemberService memberService = new MemberService(MemberJdbcRepository.memberJdbcRepository);

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Long save(Member member) {
        if(!isExistUser(member.getUserId())){
            //todo : 예외처리로 변경
            return memberRepository.save(member);
        }
        logger.debug("이미 저장 된 유저입니다");
        throw new BadRequestException("이미 저장 된 유저 입니다.");
    }

    public Member getUserByIdAndPw(String userId, String password) throws BadRequestException {
        Member member = memberRepository.findById(userId);
        if(member == null){
            return null;
        }
        if(member.getPassword().equals(password)){
            return member;
        }
        return null;
    }

    public boolean isExistUser(String userId) {
        return memberRepository.isExists(userId);
    }

    public Member get(Long userId) throws BadRequestException {
        return memberRepository.findById(userId);
    }

    public List<Member> getAll() {
        return memberRepository.findAll();
    }

}
