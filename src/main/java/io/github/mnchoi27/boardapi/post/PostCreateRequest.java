package io.github.mnchoi27.boardapi.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(
        @NotNull Long memberId,
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 10000) String content) {
}
