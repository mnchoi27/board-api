package io.github.mnchoi27.boardapi.member;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    void registerSucceeds() {
        // given
        String email = "user@test.com";
        String nickname = "tester";
        given(memberRepository.existsByEmail(email)).willReturn(false);
        given(memberRepository.saveAndFlush(any(Member.class))).willAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            ReflectionTestUtils.setField(member, "id", 1L);
            return member;
        });

        // when
        MemberResponse response = memberService.register(email, nickname);

        // then
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo(email);
        assertThat(response.nickname()).isEqualTo(nickname);
    }

    @Test
    void registerThrowsWhenEmailExists() {
        // given
        String email = "user@test.com";
        String nickname = "tester";
        given(memberRepository.existsByEmail(email)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.register(email, nickname))
                .isInstanceOf(DuplicateEmailException.class);
        then(memberRepository).should(never()).saveAndFlush(any(Member.class));
    }

    @Test
    void registerThrowsWhenSaveViolatesUniqueConstraint() {
        // given
        String email = "user@test.com";
        String nickname = "tester";
        given(memberRepository.existsByEmail(email)).willReturn(false);
        given(memberRepository.saveAndFlush(any(Member.class)))
                .willThrow(new DataIntegrityViolationException("duplicate email"));

        // when & then
        assertThatThrownBy(() -> memberService.register(email, nickname))
                .isInstanceOf(DuplicateEmailException.class);
    }
}
