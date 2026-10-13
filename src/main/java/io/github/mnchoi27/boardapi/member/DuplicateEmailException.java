package io.github.mnchoi27.boardapi.member;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Member already exists with email: " + email);
    }
}
