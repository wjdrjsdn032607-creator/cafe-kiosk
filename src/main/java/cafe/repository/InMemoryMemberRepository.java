package cafe.repository;

import cafe.domain.member.Member;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryMemberRepository implements MemberRepository {

    private final Map<String, Member> members = new HashMap<>();
    private int sequence = 0;

    @Override
    public Member save(Member member) {
        members.put(member.getId(), member);
        return member;
    }

    @Override
    public Optional<Member> findById(String id) {
        return Optional.ofNullable(members.get(id));
    }

    @Override
    public String nextId() {
        String id;
        do {
            sequence++;
            id = String.format("M%03d", sequence);
        } while (members.containsKey(id));
        return id;
    }
}
