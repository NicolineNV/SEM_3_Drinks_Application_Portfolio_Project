**OBS! - This is a school project - not a fully functioning application and still a work-in-progress.
In this project I am using TheCocktailDB.com free API.

# DrinksAPI– Cocktail Recommendation API

## Vision

This 3rd semester project is a backend REST API that recommends cocktails based on a short taste quiz. Rather than presenting users with a static library of recipes to scroll through, the application guides them through the quiz designed to pinpoint exactly the kind of drink they're in the mood for. The user answers questions about drink type, base spirit and flavour preferences, and the system returns the cocktail whose flavour profile matches best.

---

## Links

Deployed application:
https://drinksapi.nvisby.com/api/v1

Source code repository:
https://github.com/NicolineNV/SEM_3_Drinks_Application_Portfolio_Project.git

Portfolio website:
https://nicolinenv.github.io/SEM_3_Portfolio_Project/

---

# Architecture

## System Overview

The project is a layered Java backend. Each layer only talks to the layer below it:

- **Controller layer** (Javalin): HTTP, request validation, status codes
- **Service layer**: business logic, mapping between entities and DTOs
- **DAO layer**: database access through JPA/Hibernate
- **Entities**: JPA entities mirroring the database schema

Technologies used:

- Java 25
- Javalin 6.3.0
- JPA / Hibernate 7.2.3.Final
- Postgres 42.7.7
- Maven = maven compiler plugin 3.16.0
- Maven = fat jar built with maven-shade-plugin 3.6.0
- JWT authentication (com.auth0 java-jwt 4.6.1)
- BCrypt password hashing = jbcrypt 0.4
- Lombok 1.18.42
- Logback 1.5.27
- Org.slf4j 2.0.17
- Jackson 2.21.3
- JUnit 6.0.2
- Testcontainers 2.0.3
- REST-assured 5.5.1
- Docker, GitHub Actions, Docker Hub, Watchtower, Caddy (deployment)

---

## Architecture Diagram

```
Client
  │  HTTPS
  ▼
Caddy (reverse proxy, automatic TLS)
  │
  ▼
Javalin  ──►  AccessHandler (JWT + roles)
  │
  ▼
Controller  ──►  Service  ──►  DAO  ──►  PostgreSQL
                   │
                   └── DTOs (entities never leave the service layer)
```

Deployment pipeline:

```
git push (main) → GitHub Actions (build + tests) → Docker Hub → Watchtower on the droplet → new container
```

---

## Key Design Decisions

**Schema first.**
The database is defined in `database/schema.sql`, and the entities mirror the tables. In production Hibernate runs with `hbm2ddl=validate`, so the application fails on startup if entities and schema do not match, instead of silently changing the database. This way I can have full control over my data in my database, without having to worry that something changes overtime. 

**Entities never leave the service layer.**
Controllers only see request and response DTOs. This keeps `passwordHash` and lazy relations out of the JSON responses.

**Lazy relations with explicit fetching.**
Relations are `LAZY`, and each use case loads what it needs with `JOIN FETCH`. Each DAO method opens its own `EntityManager`.

**Constructor injection of `EntityManagerFactory`.**
The factory is passed through routes, controllers, services and DAOs. This lets the REST tests run the entire application against a Testcontainers PostgreSQL database.

**Quiz logic with the Strategy pattern.**
The quiz filters on drink family (required) and spirit (optional). It builds a flavour profile per cocktail by summing the intensities of its ingredients, ranks the dimensions, and picks the cocktail with the smallest distance to the user's answers. The distance calculation is a `ScoringStrategy`; `EuclideanDistanceStrategy` is injected into `QuizService`.

**Authentication and authorisation.**
Users register and log in and receive a JWT. Each route is annotated with a role (`ANYONE`, `USER`, `ADMIN`). An `AccessHandler` runs before every matched route and checks the `Authorization: Bearer <token>` header.

- No token or invalid token: `401`
- Valid token but wrong role: `403`
- `ADMIN` passes everywhere

The user's identity (for favourites and admin actions) is always read from the token, never from the request body or URL. Passwords are hashed with BCrypt, and wrong username and wrong password give the same error message.

**Error handling.**
Business errors are thrown as `ApiException` (status code and message). Javalin exception handlers turn them into JSON. Validation errors give `400`, and unexpected errors give `500` and are logged.

**Secrets.**
Secrets (`SECRET_KEY`, database password) are only read from environment variables in production and are never committed.

---

# Data Model

## ERD

<img width="1025" height="552" alt="Skærmbillede 2026-10-28 kl  12 56 34" src="https://github.com/user-attachments/assets/c7d226d8-b763-4b1c-945f-cc28fb44495d" />


Textual overview:

```
users 1 ──── * user_favorite_cocktail * ──── 1 cocktail

cocktail_family 1 ──── * cocktail

spirit   1 ──── * cocktail      (optional)
liqueur  1 ──── * cocktail      (optional)
mixer    1 ──── * cocktail      (optional)
syrup    1 ──── * cocktail      (optional)
garnish  1 ──── * cocktail      (optional)

spirit  * ──── * flavor_tag   (spirit_flavor_tag,  with intensity 1-5)
liqueur * ──── * flavor_tag   (liqueur_flavor_tag, with intensity 1-5)
mixer   * ──── * flavor_tag   (mixer_flavor_tag,   with intensity 1-5)
syrup   * ──── * flavor_tag   (syrup_flavor_tag,   with intensity 1-5)
```

The database has 14 tables and is seeded with 222 cocktails imported once from TheCocktailDB. Recipes that do not fit the model (for example: one ingredient per slot) were skipped and logged to a file.

---

## Important Entities

### User (`users`)

A registered user.

Fields: `id`, `username` (unique), `email` (unique), `password_hash`, `role` (`USER` or `ADMIN`)

### Cocktail (`cocktail`)

A cocktail recipe.

Fields: `id`, `name` (unique), `description`, `instructions`, `cocktail_family_id` (required), and optional foreign keys `spirit_id`, `liqueur_id`, `mixer_id`, `syrup_id`, `garnish_id`.
The rule "at least one ingredient" is enforced in the service layer, not in the database.

### CocktailFamily (`cocktail_family`)

The drink category, for example cocktails or mocktails. Non-alcoholic drinks are a family, not a separate flag.

Fields: `id`, `name`, `description`

### Spirit, Liqueur, Mixer, Syrup, Garnish

Ingredient tables with the same structure.

Fields: `id`, `name` (unique), `description`

### FlavorTag (`flavor_tag`)

A flavour dimension used by the quiz.

Fields: `id`, `name` (unique)

### Ingredient–FlavorTag join tables

`spirit_flavor_tag`, `liqueur_flavor_tag`, `mixer_flavor_tag` and `syrup_flavor_tag` link an ingredient to a flavour tag with an `intensity` between 1 and 5. They are entities with composite keys.

### UserFavoriteCocktail (`user_favorite_cocktail`)

A user's saved cocktail.

Fields: `user_id`, `cocktail_id`, `saved_at`

---

# API Documentation

Base URL: `https://drinksapi.nvisby.com/api/v1`

Protected endpoints require the header `Authorization: Bearer <token>`.

## Overview

| Method | URL | Access | Request body | Response | Errors |
|--------|-----|--------|--------------|----------|--------|
| POST | /auth/register | Anyone | RegisterRequest (1) | 201 AuthResponse (3) | 400, 409 |
| POST | /auth/login | Anyone | LoginRequest (2) | 200 AuthResponse (3) | 400, 401 |
| GET | /cocktails | Anyone | | [cocktail, ...] (4) | |
| GET | /cocktails/{id} | Anyone | | cocktail (4) | 404 |
| POST | /cocktails | Admin | CreateCocktail (5) | 201 cocktail (4) | 400, 401, 403 |
| GET | /families | Anyone | | [option, ...] (6) | |
| GET | /spirits | Anyone | | [option, ...] (6) | |
| GET | /flavor-tags | Anyone | | [option, ...] (6) | |
| GET | /liqueurs | Admin | | [option, ...] (6) | 401, 403 |
| GET | /mixers | Admin | | [option, ...] (6) | 401, 403 |
| GET | /syrups | Admin | | [option, ...] (6) | 401, 403 |
| GET | /garnishes | Admin | | [option, ...] (6) | 401, 403 |
| POST | /quiz | Anyone | QuizAnswer (7) | cocktail (4) | 400 |
| GET | /favorites | User | | [cocktail, ...] (4) | 401 |
| POST | /favorites/{cocktailId} | User | | 201 cocktail (4) | 401, 404 |
| DELETE | /favorites/{cocktailId} | User | | 204 No Content (8) | 401, 404 |

## Request and Response Formats

**(1) RegisterRequest**

```json
{
  "username": "john",
  "email": "john@example.com",
  "password": "secretPassword"
}
```

Username must be 3 or more characters, the password 8-72 characters, and the email must be valid.

**(2) LoginRequest**

```json
{
  "username": "john",
  "password": "secretPassword"
}
```

**(3) AuthResponse**

```json
{
  "token": "<jwt>",
  "user": { "id": 1, "username": "john", "email": "john@example.com", "role": "USER" }
}
```

**(4) Cocktail**

```json
{
  "id": 1,
  "name": "A1",
  "description": "- 1 3/4 shot Gin\n- 1 Shot Grand Marnier\n- 1/4 Shot Lemon Juice\n- 1/8 Shot Grenadine",
  "instructions": "Pour all ingredients into a cocktail shaker, mix and serve over ice into a chilled glass.",
  "cocktailFamily": "Cocktail",
  "spirit": "Gin",
  "liqueur": "Grand Marnier",
  "mixer": "Lemon Juice",
  "syrup": "Grenadine",
  "garnish": null
}
```

**(5) CreateCocktail**

```json
{
  "id": 300,
  "name": "Cocktail tester",
  "description": "d",
  "instructions": "i",
  "cocktailFamily": "Cocktail",
  "spirit": "Spirit",
  "liqueur": null,
  "mixer": null,
  "syrup": null,
  "garnish": null
}
```
At least one ingredient is required.

**(6) Option**

```json
{ "id": 1, "name": "Gin", "description": "..." }
```

**(7) QuizAnswer**

```json
{
  "cocktailFamilyId": 1,
  "spiritId": 2,
  "tasteAnswers": { "1": 4, "2": 2, "3": 5, "4": 1, "5": 3 }
}
```
`spiritId` is optional. `tasteAnswers` maps a flavour tag id to an intensity from 1 to 5.

**(8) NoContent**

```json
{
  <Response body is empty>
}
```

## Example: Login and Favorites

```
POST /api/v1/auth/login
```

Request:

```json
{ "username": "john", "password": "secretPassword" }
```

Response `200 OK`:

```json
{ "token": "<jwt>", "user": { "id": 1, "username": "john", "email": "john@example.com", "role": "USER" } }
```

Favorites
```
GET /api/v1/favorites
```

Response `200 OK`:

```json
[
  {
    "id": 6,
    "name": "Aviation",
    "description": "- 4.5 cl Gin\n- 1.5 cl lemon juice\n- 1.5 cl maraschino liqueur",
    "instructions": "Add all ingredients into cocktail shaker filled with ice. Shake well and strain into cocktail glass. Garnish with a cherry.",
    "cocktailFamily": "Cocktail",
    "spirit": "Gin",
    "liqueur": "Maraschino Liqueur",
    "mixer": "Lemon Juice",
    "syrup": null,
    "garnish": null
  },
  {
    "id": 7,
    "name": "Applecar",
    "description": "- 1 oz Applejack\n- 1 oz Triple sec\n- 1 oz Lemon juice",
    "instructions": "Shake all ingredients with ice, strain into a cocktail glass, and serve.",
    "cocktailFamily": "Ordinary Drink",
    "spirit": "Apple Brandy",
    "liqueur": "Triple Sec",
    "mixer": "Lemon Juice",
    "syrup": null,
    "garnish": null
  }
]
```

## Errors

All errors are reported in this format, where `status` matches the HTTP status code:

```json
{ "message": "Explains the problem", "status": 400 }
```

Examples:

`401` (wrong login):

```json
{ "message": "Wrong username or password", "status": 401 }
```

`400` (invalid body):

```json
{ "message": "Invalid request", "status": 400 }
```

`404` (unknown cocktail):

```json
{ "message": "Cocktail does not exist: {id}", "status": 404 }
```

Other errors: `401` for a missing or invalid token, `403` when the role is not allowed.

---

# User Stories

1. **As a User**, I want to create an account and log in, so that my saved recipes and preferences are tied to my profile.
2. **As a User**, I want to save the recipes that I like on my profile, so that I can easily find them again.
3. **As a User**, I want to answer a series of questions, so that the app can find the recipe that best suits my mood.
4. **As a User**, I want to choose the kind of "drink family" that I want, so that the app knows what kind of recipe to recommend.
5. **As a User**, I want to describe the taste of the drink I want, so that the recipe I get matches my preferred taste.
6. **As a User**, I want to choose what kind of spirit goes into my drink, so that the app can find the kind of drink I want.
7. **As a User** who has answered all the questions, I want the recommended recipe to be displayed, so that I can follow it and make my drink.
   
8. **As an Admin**, I want to log in to my account and have access to admin-only features, so that I can maintain and manage the application.
9. **As an Admin**, I want to add new drink recipes to the database, so that the app keeps up with drinking trends and user demand.


There were also other user stories, that unfortunately had to be cut because of the lack of time.

---

# Testing

- **Unit tests:** `EuclideanDistanceStrategy` and `TokenSecurity`.
- **Integration tests:** DAO against PostgreSQL in Testcontainers.
- **REST-assured tests:** every controller (auth, lookups, quiz, cocktails, favourites, admin) against a Javalin instance on a random port, with a real database in Testcontainers.
- **request.http:** runs all endpoints against `local` or `prod` with IntelliJ's HTTP client. Credentials are kept in a git-ignored `http-client.private.env.json`.

Tests run in GitHub Actions on every push to `main`. The Docker image is only pushed if the tests pass.

---
