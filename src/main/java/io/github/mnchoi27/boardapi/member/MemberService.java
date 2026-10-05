package io.github.mnchoi27.boardapi.member;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public MemberResponse register(String email, String nickname) {
        Member member = new Member(email, nickname);

        Member saved = memberRepository.save(member);

        return MemberResponse.from(saved);
    }
}
