package io.github.mnchoi27.boardapi.post;

public record PostCreateRequest(Long memberId, String title, String content) {
}
