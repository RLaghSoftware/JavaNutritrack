# NutriTrack — Authentication setup

## Prerequisites

- MySQL on `localhost:3306`, schema **`nutritrackjava`**
- JDK 17+, Maven, Node.js
- `.env` in project root (copy from `.env.example`)

## 1. Database

Run the auth section in [db/changelog.sql](db/changelog.sql) in MySQL Workbench (creates `users` and `refresh_tokens`).

## 2. Environment variables

```env
MYSQL_PASSWORD=your_mysql_root_password
JWT_SECRET=use_a_long_random_string_at_least_32_characters
```

Spring Boot reads these from the environment when you start the backend.

## 3. Run backend (port 8080)

```powershell
cd backend
$env:MYSQL_PASSWORD = "your_mysql_root_password"
$env:JWT_SECRET = "your_long_jwt_secret"
mvn spring-boot:run
```

## 4. Run frontend (port 3000)

```powershell
cd ui
npm install
npm run dev
```

Open http://localhost:3000 — sign up, then use the dashboard.

## Example API requests

**Signup**

```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"jane\",\"email\":\"jane@example.com\",\"password\":\"password123\"}"
```

**Login**

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"email\":\"jane@example.com\",\"password\":\"password123\"}"
```

**Protected dashboard** (replace `TOKEN`)

```bash
curl http://localhost:8080/api/dashboard \
  -H "Authorization: Bearer TOKEN"
```

**Refresh token**

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"YOUR_REFRESH_TOKEN\"}"
```

## JWT flow

1. User signs up or logs in → server validates credentials, returns **access JWT** + **refresh token**.
2. React stores tokens (`sessionStorage` by default; `localStorage` if “Remember me”).
3. Axios adds `Authorization: Bearer <accessToken>` on each API call.
4. `JwtAuthenticationFilter` validates the JWT and sets the Spring Security context.
5. Protected controllers (`/api/dashboard`, `/api/users/me`) require a valid token.
6. On **401**, the UI tries `/api/auth/refresh`; if that fails, tokens are cleared and the user is sent to `/login`.

## Protected routes

| Layer | Mechanism |
|-------|-----------|
| Backend | `SecurityConfig` — `/api/auth/**` and `/api/hello` public; `/api/dashboard/**`, `/api/users/**` authenticated |
| Frontend | `ProtectedRoute` — redirects to `/login` if no user in `AuthContext` |

## Secure cookies (discussion)

This app uses **Bearer tokens in memory/storage** (typical for SPAs). **HttpOnly cookies** reduce XSS token theft but need CSRF protection and same-site cookie configuration. To switch later: set JWT in an HttpOnly cookie from the backend and enable CSRF for cookie-based sessions.

## Tests

```powershell
cd backend
mvn test
```
