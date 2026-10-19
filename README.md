# board-api

A REST API for a simple bulletin board where members can create and manage posts and comments.

## Tech Stack

- Java 25
- Spring Boot 4.1
    - Spring Web MVC
    - Spring Data JPA
    - Bean Validation (Hibernate Validator)
- H2 Database
- Gradle (Groovy DSL)
- springdoc-openapi

## Getting Started

### Prerequisites

- JDK 25

### Run

Clone the repository and run the application with Gradle Wrapper.

```bash
./gradlew bootRun
```

The application will be available at:

http://localhost:8080

http://localhost:8080/swagger-ui/index.html

## API

- `POST /members` — Register a member (201)
- `POST /posts` — Create a post (201)
- `GET /posts` — List posts by creation time, newest first (200)
- `GET /posts/{id}` — Get a post (200)
- `PUT /posts/{id}` — Update a post (200)
- `DELETE /posts/{id}` — Delete a post and its comments (204)
- `POST /posts/{postId}/comments` — Add a comment to a post (201)
- `GET /posts/{postId}/comments` — List a post's comments by creation time, oldest first (200)
- `DELETE /comments/{commentId}` — Delete a comment (204)

## Design Decisions

- Organize code by domain, keeping each feature's components together.
- Use Hibernate timestamp annotations to manage creation and update times.
- Sort posts newest first and comments oldest first, both by creation time.
- Include the author's ID and nickname in post and comment responses.
- Return errors in the RFC 9457 Problem Details format: 400 for invalid requests, 404 for missing resources, and 409 for duplicate emails. Validation failures list each field error under `errors`.
- Delete a post's comments through an `ON DELETE CASCADE` foreign key, keeping the post-comment relation unidirectional.

## Known Limitations

- Post and comment creation accept any member ID as the author.
- Update and delete operations do not yet verify that the requester owns the post or comment.
- Data is stored in an in-memory H2 database and is lost on restart.