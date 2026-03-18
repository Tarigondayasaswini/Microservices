# Frontend Migration Guide & API Changes

Moving from the monolithic Spring Boot application to the new Microservices Architecture introduces several changes to how the Frontend communicates with the Backend.

## 1. Single Entry Point
**OLD**: The frontend made calls directly to port `8081` (assuming that was the monolith port).
**NEW**: ALL traffic must go through the **API Gateway** on port `8080`.
Change your `REACT_APP_API_URL` to point to `http://localhost:8080/api/v1`.

## 2. API Routing Rules
The API Gateway routes traffic based on path prefixes:
- Requests starting with `/api/v1/auth` -> **User Service**
- Requests starting with `/api/v1/users` -> **User Service**
- Requests starting with `/api/v1/songs` -> **Catalog Service**
- Requests starting with `/api/v1/albums` -> **Catalog Service**
- Requests starting with `/api/v1/artists` -> **Catalog Service**
- Requests starting with `/api/v1/playlists` -> **Playlist Service**
- Requests starting with `/api/v1/likes` -> **Playlist Service**
- Requests starting with `/api/v1/playback` -> **Playback Service**
- Requests starting with `/api/v1/ads` -> **Analytics Service**
- Requests starting with `/api/v1/subscriptions` -> **Analytics Service**

## 3. JWT Authentication Handling
- The Gateway automatically intercepts all requests.
- Endpoints like `/api/v1/auth/**` and GET requests to `/api/v1/songs/**` are generally public.
- Make sure to pass the JWT in the `Authorization: Bearer <token>` header for protected endpoints.
- If the token is missing or invalid, the Gateway will return a `401 Unauthorized`. Make sure your Axios interceptors handle this by clearing local storage and redirecting to the login page.

## 4. Error Responses
All endpoints across all microservices now return a standard wrapper:
```javascript
{
  "success": false,
  "message": "Error description",
  "data": null,
  "errors": [
    // Validation errors here
  ],
  "timestamp": "..."
}
```
Update frontend components (like the Registration form or Login page) to read from `error.response.data.message` rather than assuming plain text or generic Spring Boot error JSON.
