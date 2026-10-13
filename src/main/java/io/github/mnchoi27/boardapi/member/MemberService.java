package io.github.mnchoi27.boardapi.member;

import org.springframework.dao.DataIntegrityViolationException;
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
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        Member member = new Member(email, nickname);
        try {
            Member saved = memberRepository.saveAndFlush(member);
            return MemberResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException(email);
        }
    }
}
