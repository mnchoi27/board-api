package io.github.mnchoi27.boardapi.comment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.mnchoi27.boardapi.member.Member;
import io.github.mnchoi27.boardapi.member.MemberNotFoundException;
import io.github.mnchoi27.boardapi.member.MemberRepository;
import io.github.mnchoi27.boardapi.post.Post;
import io.github.mnchoi27.boardapi.post.PostNotFoundException;
import io.github.mnchoi27.boardapi.post.PostRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository,
            MemberRepository memberRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public CommentResponse create(Long postId, Long memberId, String content) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));

        Comment comment = new Comment(content, post, member);
        Comment saved = commentRepository.save(comment);

        return CommentResponse.from(saved);
    }
}
