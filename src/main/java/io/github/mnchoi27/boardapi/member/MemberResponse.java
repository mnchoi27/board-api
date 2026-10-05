package io.github.mnchoi27.boardapi.member;

public record MemberResponse(Long id, String email, String nickname) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getEmail(), member.getNickname());
    }
}
