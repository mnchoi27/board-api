package io.github.mnchoi27.boardapi.comment;

public class CommentNotFoundException extends RuntimeException {

    public CommentNotFoundException(Long id) {
        super("Comment not found with id: " + id);
    }
}
