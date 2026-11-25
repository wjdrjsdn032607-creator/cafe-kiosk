package cafe.repository;

import cafe.domain.member.Member;

import java.util.Optional;

public interface MemberRepository {

    Member save(Member member);

    Optional<Member> findById(String id);

    /** 새 회원 ID를 만든다 (M001, M002 ...). */
    String nextId();
}
