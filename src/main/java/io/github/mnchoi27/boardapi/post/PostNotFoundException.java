package io.github.mnchoi27.boardapi.post;

public class PostNotFoundException extends RuntimeException {

    public PostNotFoundException(Long id) {
        super("Post not found with id: " + id);
    }
}
