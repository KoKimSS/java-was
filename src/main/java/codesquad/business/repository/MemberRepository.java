package codesquad.business.repository;

import codesquad.business.domain.Member;

import java.util.List;

public interface MemberRepository extends Repository<Long, Member> {
    boolean isExists(String userId);

     Member findById(String userId);

    List<Member> findAll();
}
