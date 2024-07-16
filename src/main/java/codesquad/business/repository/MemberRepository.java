package codesquad.business.repository;

import codesquad.business.domain.Member;

public interface MemberRepository extends Repository<Long, Member> {
    boolean isExists(String userId);
}
