package io.github.mnchoi27.boardapi.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberCreateRequest(
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(max = 20) String nickname) {
}
