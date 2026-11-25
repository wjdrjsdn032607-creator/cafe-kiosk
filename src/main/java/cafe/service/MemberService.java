package cafe.service;

import cafe.common.CafeException;
import cafe.domain.member.Member;
import cafe.repository.MemberRepository;

public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member register(String name) {
        Member member = new Member(memberRepository.nextId(), name);
        return memberRepository.save(member);
    }

    public Member find(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new CafeException("존재하지 않는 회원이에요: " + id));
    }
}
