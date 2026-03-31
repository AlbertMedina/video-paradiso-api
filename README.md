# Video Paradiso (API)

## Description
This project recreates the classic video rental store experience in a digital environment, allowing users to browse, rent, and rate movies. The application supports different user roles, such as regular users and administrators, each with dedicated features to interact with the movie catalog and manage the platform.
The system focuses on providing an intuitive way to explore content while enforcing role-based access and permissions, ensuring a structured and well-managed movie rental experience.
> This repository contains the Backend (API). The Frontend application can be found in this repository: [video-paradiso-frontend](https://github.com/AlbertMedina/video-paradiso-frontend.git)

## Architecture & Tech Stack
Built with Java 21 and Spring Boot 3.5.8, this project follows a Hexagonal Architecture approach to keep the core business logic independent from frameworks, infrastructure, and external systems.
- **Domain Layer**  
  Contains the core business logic and domain models (User, Movie, Rental, Review, and Favourite), fully isolated from infrastructure and framework concerns.
- **Application Layer**  
  Defines use cases and application services, coordinating domain logic through input and output ports.
- **Infrastructure Layer**  
  Implements persistence, security, and external integrations, including database access, JWT authentication, and file handling for movie posters.
- **Persistence**  
  MySQL database accessed through Spring Data JPA, ensuring a clean separation between domain models and data storage.
- **Security**  
  Stateless authentication using JWT, with role-based access control (USER / ADMIN) enforced at the API level.
- **Caching**  
  Application-level caching using Spring Cache to improve performance and reduce database load on frequently accessed resources.
- **Testing**  
  Integration testing using Spring Boot Test and MockMvc, with Testcontainers providing Dockerized database environments to ensure realistic and reproducible test execution.
- **Documentation**  
  Interactive API documentation generated with OpenAPI (Swagger UI), allowing easy exploration and testing of available endpoints.

## Endpoints

### Authentication
| Method | Endpoint         | Description         | Request Body                                                                                                            |
|--------|------------------|---------------------|-------------------------------------------------------------------------------------------------------------------------|
| POST   | `/auth/register` | Register a new user | `{ "name": "User", "surname": "Example", "username": "user123", "email": "user@example.com", "password": "secret123" }` |
| POST   | `/auth/login`    | Authenticate a user | `{ "loginIdentifier": "user123", "password": "secret123" }`                                                             |

### Users
| Method | Endpoint          | Description                    | Request Body |
|--------|-------------------|--------------------------------|--------------|
| GET    | `/me`             | Get authenticated user details | N/A          |
| GET    | `/users`          | Get all users (ADMIN)          | N/A          |
| GET    | `/users/{userId}` | Get user details by ID (ADMIN) | N/A          |
| DELETE | `/users/{userId}` | Remove a user (ADMIN)          | N/A          |

### Movies
| Method | Endpoint             | Description                             | Request Body                                                                                                             |
|--------|----------------------|-----------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| POST   | `/movies`            | Add a new movie (ADMIN)                 | `multipart/form-data`<br>`movie: { "title": "Example", "year": 2026, "genre": "Example", "duration": 120, "director": "Example", "synopsis": "Example", "numberOfCopies": 3 }`<br>`poster: file (optional)` |
| PUT    | `/movies/{movieId}`  | Update movie information (ADMIN)        | `{ "title": "Example", "year": 2026, "genre": "Example", "duration": 120, "director": "Example", "synopsis": "Example"}` |
| DELETE | `/movies/{movieId}`  | Remove a movie (ADMIN)                  | N/A                                                                                                                      |
| GET    | `/movies/{movieId}`  | Get movie details                       | N/A                                                                                                                      |
| GET    | `/movies`            | Get all movies with filters and sorting | Query params                                                                                                             |
| GET    | `/movies/genres`     | Get all available movie genres          | N/A                                                                                                                      |

### Rentals
| Method | Endpoint                    | Description                             | Request Body       |
|--------|-----------------------------|-----------------------------------------|--------------------|
| POST   | `/rentals`                  | Rent a movie                            | `{ "movieId": 1 }` |
| DELETE | `/rentals/{movieId}`        | Return a rented movie                   | N/A                |
| GET    | `/me/rentals`               | Get authenticated user active rentals   | N/A                |
| GET    | `/users/{userId}/rentals`   | Get rentals by user (ADMIN)             | N/A                |
| GET    | `/movies/{movieId}/rentals` | Get rentals by movie (ADMIN)            | N/A                |
| GET    | `/me/rentals/{movieId}`     | Check if user has rented a movie        | N/A                |

### Reviews
| Method | Endpoint                    | Description                    | Request Body                                          |
|--------|-----------------------------|--------------------------------|-------------------------------------------------------|
| POST   | `/reviews`                  | Add a review to a movie        | `{ "movieId": 1, "rating": 5, "comment": "Example" }` |
| DELETE | `/reviews/{reviewId}`       | Remove a review                | N/A                                                   |
| GET    | `/movies/{movieId}/reviews` | Get all reviews for a movie    | N/A                                                   |
| GET    | `/me/reviews/{movieId}`     | Check if user reviewed a movie | N/A                                                   |

### Favourites
| Method | Endpoint                   | Description                  | Request Body       |
| ------ | -------------------------- | ---------------------------- | ------------------ |
| POST   | `/favourites`              | Add movie to favourites      | `{ "movieId": 1 }` |
| DELETE | `/favourites/{movieId}`    | Remove movie from favourites | N/A                |
| GET    | `/me/favourites`           | Get user favourite movies    | N/A                |
| GET    | `/me/favourites/{movieId}` | Check if movie is favourite  | N/A                |

> Authentication is handled using JWT. Protected endpoints require the token to be sent in the `Authorization` header as `Bearer <token>`.

## Installation
1. Clone repository (https://github.com/AlbertMedina/video-paradiso-api.git).
```
git clone https://github.com/AlbertMedina/video-paradiso-api.git
```
2. Navigate to project folder.
```
cd video-paradiso-api
```
3. Ensure Docker is installed and running.

## Configuration
This project uses Cloudinary for movie poster storage. Before running the application, you must set up your environment variables:
1. Copy the example environment file.
```
cp .env.example .env
```
2. Open the `.env` file and fill in your Cloudinary credentials:
- CLOUDINARY_CLOUD_NAME: Your Cloudinary Cloud Name.
- CLOUDINARY_API_KEY: Your Cloudinary API Key.
- CLOUDINARY_API_SECRET: Your Cloudinary API Secret.
💡 Optional: If you don't want to use Cloudinary, you can leave these variables blank. The application will still work perfectly for browsing, renting, and managing movies. The only limitation is that you won't be able to upload movie posters (movies will simply appear without an image).
Note: The `.env` file is ignored by Git to keep your credentials secure. Ensure you have a valid Cloudinary account to enable image uploads.

## Execution
1. Start the application and databases with Docker Compose:
```
docker-compose up --build
```
This will start MySQL and the Spring Boot API.
The API will be available at:
```
http://localhost:8080
```
The API documentation will be available via Swagger at:
```
http://localhost:8080/swagger-ui.html
```
