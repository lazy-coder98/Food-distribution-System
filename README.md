# Food Distribution System

Production-grade Spring Boot backend for a Food Donation & Distribution System that connects restaurants with NGOs and supports map-based discovery of available food posts.

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Security with JWT
- Spring Data MongoDB with MongoDB Atlas support
- MongoRepository, MongoTemplate, `@Document`, `ObjectId`
- Bean Validation
- Lombok
- Swagger/OpenAPI
- Docker and Docker Compose

## Quick Start

1. Create an environment file:

```bash
cp .env.example .env
```

2. Set `MONGODB_URI` to your MongoDB Atlas connection string.

3. Run locally:

```bash
mvn spring-boot:run
```

4. Or run with Docker:

```bash
docker compose up --build
```

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

## MongoDB Atlas Configuration

Use an Atlas connection string like:

```text
mongodb+srv://<username>:<password>@<cluster-url>/food_distribution_system?retryWrites=true&w=majority
```

Set it as:

```bash
MONGODB_URI=mongodb+srv://...
```

The application enables automatic index creation, including the `2dsphere` index used by nearby food search.

## Environment Variables

| Variable | Description |
| --- | --- |
| `SERVER_PORT` | API port, defaults to `8080` |
| `MONGODB_URI` | MongoDB Atlas or local MongoDB connection string |
| `JWT_SECRET` | JWT HMAC secret, use at least 32 bytes |
| `JWT_EXPIRATION_MS` | Token lifetime in milliseconds |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend origins |
| `ADMIN_EMAIL` | Optional bootstrap admin email |
| `ADMIN_PASSWORD` | Optional bootstrap admin password |
| `EXPIRE_FOOD_POSTS_CRON` | Cron for auto-expiring food posts |

## Main API Groups

### Authentication

- `POST /api/auth/register`
- `POST /api/auth/login`

Roles: `ADMIN`, `RESTAURANT`, `NGO`. Public registration allows `RESTAURANT` and `NGO`; admins are created via bootstrap env vars.

### Users

- `GET /api/users/me`

### Restaurant Profiles

- `POST /api/restaurants/me`
- `PUT /api/restaurants/me`
- `GET /api/restaurants/me`

### NGO Profiles

- `POST /api/ngos/me`
- `PUT /api/ngos/me`
- `GET /api/ngos/me`

### Food Posts

- `POST /api/food-posts`
- `GET /api/food-posts?status=AVAILABLE&page=0&size=20&sort=createdAt,desc`
- `GET /api/food-posts/nearby?latitude=28.6139&longitude=77.2090&radiusKm=10`
- `GET /api/food-posts/{id}`
- `GET /api/food-posts/me`
- `PUT /api/food-posts/{id}`
- `PATCH /api/food-posts/{id}/status`
- `DELETE /api/food-posts/{id}`

### Claims

- `POST /api/claims`
- `GET /api/claims`
- `GET /api/claims/me`
- `GET /api/claims/{id}`
- `PATCH /api/claims/{id}/approve`
- `PATCH /api/claims/{id}/reject`
- `PATCH /api/claims/{id}/complete`

### Admin

- `GET /api/admin/users`
- `GET /api/admin/users/{id}`
- `DELETE /api/admin/users/{id}`
- `DELETE /api/admin/food-posts/{id}`
- `GET /api/admin/claims`
- `GET /api/admin/dashboard`

## Security

Send JWT tokens using:

```text
Authorization: Bearer <token>
```

The app is stateless, uses BCrypt password hashing, and enforces role-based authorization at both route and service levels for sensitive workflows.

## Map Support

Food posts store:

- `latitude`
- `longitude`
- MongoDB `GeoJsonPoint location`

Nearby discovery uses `MongoTemplate.geoNear` against a `2dsphere` index and returns distance in meters.

## Postman

Import:

```text
postman/Food_Distribution_System.postman_collection.json
```

Set collection variables:

- `baseUrl`
- `restaurantToken`
- `ngoToken`
- `adminToken`

## Build

```bash
mvn clean package
```

## Deploy on Render

This repository includes `render.yaml` for Render Blueprint deployment.

1. Open Render and create a new Blueprint from this GitHub repository.
2. Set the required secret environment variables:
   - `MONGODB_URI`
   - `CORS_ALLOWED_ORIGINS`
   - `ADMIN_EMAIL`
   - `ADMIN_PASSWORD`
3. Render will generate `JWT_SECRET` automatically and deploy the Docker image.
