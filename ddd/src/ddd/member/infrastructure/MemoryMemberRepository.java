package ddd.member.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ddd.member.domain.Member;
import ddd.member.domain.MemberRepository;

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
