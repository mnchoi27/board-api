package io.github.mnchoi27.boardapi.member;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(Long id) {
        super("Member not found with id: " + id);
    }
}
