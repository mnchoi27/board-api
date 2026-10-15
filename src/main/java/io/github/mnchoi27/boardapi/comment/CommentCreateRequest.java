package io.github.mnchoi27.boardapi.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(
		@NotNull Long memberId,
		@NotBlank @Size(max = 1000) String content) {
}
