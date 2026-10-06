package io.github.mnchoi27.boardapi.post;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.mnchoi27.boardapi.member.Member;
import io.github.mnchoi27.boardapi.member.MemberRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostService(MemberRepository memberRepository, PostRepository postRepository) {
        this.memberRepository = memberRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public PostResponse create(Long memberId, String title, String content) {
        Member member = memberRepository.findById(memberId).orElseThrow();
        Post post = new Post(title, content, member);
        Post saved = postRepository.save(post);
        return PostResponse.from(saved);
    }
}
