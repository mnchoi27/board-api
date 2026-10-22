package io.github.mnchoi27.boardapi.comment;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.mnchoi27.boardapi.member.Member;
import io.github.mnchoi27.boardapi.member.MemberNotFoundException;
import io.github.mnchoi27.boardapi.member.MemberRepository;
import io.github.mnchoi27.boardapi.post.Post;
import io.github.mnchoi27.boardapi.post.PostNotFoundException;
import io.github.mnchoi27.boardapi.post.PostRepository;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createSucceeds() {
        // given
        Long postId = 10L;
        Long memberId = 1L;
        String content = "comment";
        Member writer = new Member("writer@test.com", "writer");
        Post post = withId(new Post("title", "content", writer), postId);
        Member member = withId(new Member("user@test.com", "tester"), memberId);
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(commentRepository.save(any(Comment.class))).willAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            return withId(comment, 100L);
        });

        // when
        CommentResponse response = commentService.create(postId, memberId, content);

        // then
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.postId()).isEqualTo(postId);
        assertThat(response.content()).isEqualTo(content);
        assertThat(response.authorId()).isEqualTo(memberId);
        assertThat(response.authorNickname()).isEqualTo("tester");
    }

    @Test
    void createThrowsWhenPostNotFound() {
        // given
        Long postId = 10L;
        Long memberId = 1L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.create(postId, memberId, "comment"))
                .isInstanceOf(PostNotFoundException.class);
        then(commentRepository).should(never()).save(any(Comment.class));
    }

    @Test
    void createThrowsWhenMemberNotFound() {
        // given
        Long postId = 10L;
        Long memberId = 1L;
        Post post = new Post("title", "content", new Member("writer@test.com", "writer"));
        given(postRepository.findById(postId)).willReturn(Optional.of(post));
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.create(postId, memberId, "comment"))
                .isInstanceOf(MemberNotFoundException.class);
        then(commentRepository).should(never()).save(any(Comment.class));
    }

    @Test
    void findAllByPostIdKeepsRepositoryOrder() {
        // given
        Long postId = 10L;
        Post post = new Post("title", "content", new Member("writer@test.com", "writer"));
        Member member = new Member("user@test.com", "tester");
        Comment first = withId(new Comment("first", post, member), 100L);
        Comment second = withId(new Comment("second", post, member), 101L);
        given(postRepository.existsById(postId)).willReturn(true);
        given(commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId)).willReturn(List.of(first, second));

        // when
        List<CommentResponse> responses = commentService.findAllByPostId(postId);

        // then
        assertThat(responses).extracting(CommentResponse::id).containsExactly(100L, 101L);
    }

    @Test
    void findAllByPostIdThrowsWhenPostNotFound() {
        // given
        Long postId = 10L;
        given(postRepository.existsById(postId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> commentService.findAllByPostId(postId))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void deleteRemovesComment() {
        // given
        Long commentId = 100L;
        Post post = new Post("title", "content", new Member("writer@test.com", "writer"));
        Member member = new Member("user@test.com", "tester");
        Comment comment = withId(new Comment("comment", post, member), commentId);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        commentService.delete(commentId);

        // then
        then(commentRepository).should().delete(comment);
    }

    @Test
    void deleteThrowsWhenCommentNotFound() {
        // given
        Long commentId = 100L;
        given(commentRepository.findById(commentId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> commentService.delete(commentId))
                .isInstanceOf(CommentNotFoundException.class);
        then(commentRepository).should(never()).delete(any(Comment.class));
    }

    private <T> T withId(T entity, Long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
