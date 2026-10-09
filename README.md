# board-api

A REST API for a simple bulletin board where members can create and manage posts and comments.

## Tech Stack

- Java 25
- Spring Boot 4.1
    - Spring Web MVC
    - Spring Data JPA
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
- `DELETE /posts/{id}` — Delete a post (204)

## Design Decisions

- Organize code by domain, keeping each feature's components together.
- Use Hibernate timestamp annotations to manage creation and update times.
- Sort posts by creation time in descending order.
- Include the author's ID and nickname in post responses.

## Known Limitations

- Request validation and consistent error handling are not implemented.
- Post creation accepts any member ID as the author.
- Update and delete operations do not yet verify post ownership.
- Data is stored in an in-memory H2 database and is lost on restart.
