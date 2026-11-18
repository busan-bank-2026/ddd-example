package ddd.member.application;

import java.util.List;

import ddd.member.domain.Member;
import ddd.member.domain.MemberRepository;

public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member findMember(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다"));
    }

    public List<Member> listMembers() {
        return memberRepository.findAll();
    }
}
