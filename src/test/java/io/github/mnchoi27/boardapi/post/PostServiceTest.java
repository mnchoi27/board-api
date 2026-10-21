package io.github.mnchoi27.boardapi.post;

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

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PostService postService;

    @Test
    void createSucceeds() {
        // given
        Long memberId = 1L;
        String title = "title";
        String content = "content";
        Member member = withId(new Member("user@test.com", "tester"), memberId);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(postRepository.save(any(Post.class))).willAnswer(invocation -> {
            Post post = invocation.getArgument(0);
            return withId(post, 10L);
        });

        // when
        PostResponse response = postService.create(memberId, title, content);

        // then
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.title()).isEqualTo(title);
        assertThat(response.content()).isEqualTo(content);
        assertThat(response.authorId()).isEqualTo(memberId);
        assertThat(response.authorNickname()).isEqualTo("tester");
    }

    @Test
    void createThrowsWhenMemberNotFound() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.create(memberId, "title", "content"))
                .isInstanceOf(MemberNotFoundException.class);
        then(postRepository).should(never()).save(any(Post.class));
    }

    @Test
    void findByIdSucceeds() {
        // given
        Long memberId = 1L;
        Long postId = 10L;
        Member member = withId(new Member("user@test.com", "tester"), memberId);
        Post post = withId(new Post("title", "content", member), postId);
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        PostResponse response = postService.findById(postId);

        // then
        assertThat(response.id()).isEqualTo(postId);
        assertThat(response.title()).isEqualTo("title");
        assertThat(response.content()).isEqualTo("content");
        assertThat(response.authorId()).isEqualTo(memberId);
        assertThat(response.authorNickname()).isEqualTo("tester");
    }

    @Test
    void findByIdThrowsWhenPostNotFound() {
        // given
        Long postId = 10L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.findById(postId))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void findAllKeepsRepositoryOrder() {
        // given
        Member member = withId(new Member("user@test.com", "tester"), 1L);
        Post older = withId(new Post("first", "content", member), 10L);
        Post newer = withId(new Post("second", "content", member), 11L);
        given(postRepository.findAllByOrderByCreatedAtDesc()).willReturn(List.of(newer, older));

        // when
        List<PostResponse> responses = postService.findAll();

        // then
        assertThat(responses).extracting(PostResponse::id).containsExactly(11L, 10L);
    }

    @Test
    void updateChangesPost() {
        // given
        Long postId = 10L;
        Member member = new Member("user@test.com", "tester");
        Post post = withId(new Post("old title", "old content", member), postId);
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        PostResponse response = postService.update(postId, "new title", "new content");

        // then
        assertThat(post.getTitle()).isEqualTo("new title");
        assertThat(post.getContent()).isEqualTo("new content");
        assertThat(response.id()).isEqualTo(postId);
        assertThat(response.title()).isEqualTo("new title");
        assertThat(response.content()).isEqualTo("new content");
    }

    @Test
    void updateThrowsWhenPostNotFound() {
        // given
        Long postId = 10L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.update(postId, "new title", "new content"))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void deleteRemovesPost() {
        // given
        Long postId = 10L;
        Member member = new Member("user@test.com", "tester");
        Post post = withId(new Post("title", "content", member), postId);
        given(postRepository.findById(postId)).willReturn(Optional.of(post));

        // when
        postService.delete(postId);

        // then
        then(postRepository).should().delete(post);
    }

    @Test
    void deleteThrowsWhenPostNotFound() {
        // given
        Long postId = 10L;
        given(postRepository.findById(postId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> postService.delete(postId))
                .isInstanceOf(PostNotFoundException.class);
        then(postRepository).should(never()).delete(any(Post.class));
    }

    private <T> T withId(T entity, Long id) {
        ReflectionTestUtils.setField(entity, "id", id);
        return entity;
    }
}
