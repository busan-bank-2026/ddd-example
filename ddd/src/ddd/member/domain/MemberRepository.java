package ddd.member.domain;

import java.util.List;
import java.util.Optional;

public interface MemberRepository {

    void save(Member member);

    Optional<Member> findById(long id);
    List<Member> findAll();
}
