package ddd.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.domain.Member;
import ddd.domain.MemberRepository;

public class MemoryMemberRepository implements MemberRepository {

    private final Map<Long, Member> members = new HashMap<>();

    @Override
    public void save(Member member) {
        members.put(member.getId(), member);
    }

    @Override
    public Optional<Member> findById(long id) {
        return Optional.ofNullable(members.get(id));
    }
}
