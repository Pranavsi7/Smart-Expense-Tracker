#  Smart Expense Tracker 

> Spring Boot 3.5 · PostgreSQL · Redis · Swagger · Docker · React/TypeScript · Mockito



---

## 🚀 What's Inside

| Feature | Details |
|---|---|
| **JWT Auth** | Register/Login · Bearer token · Role-based filter |
| **Redis Caching** | `@Cacheable` on categories & transactions · 5-min TTL · `@CacheEvict` on mutations |
| **Swagger UI** | OpenAPI 3 docs at `/api-docs-ui` · JWT auth built in |
| **Docker** | Multi-stage Dockerfile · docker-compose for all 4 services |
| **Mockito Tests** | 17 unit tests · 85%+ service-layer coverage · JaCoCo report |
| **React + TypeScript** | Vite · Pie & Bar charts (Recharts) · Axios · React Router |

---

## ⚡ Quick Start (Local — Docker)

```bash
cp .env.example .env        # fill in your values
docker-compose up --build
```

| URL | Service |
|---|---|
| http://localhost:3000 | React frontend |
| http://localhost:8080/api-docs-ui | Swagger UI |
| localhost:5435 | PostgreSQL |
| localhost:6379 | Redis |

---

## 🧪 Run Tests

```bash
cd backend
./mvnw test
# Coverage report → backend/target/site/jacoco/index.html
```

## 📡 API Reference

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/users/register` | ❌ | Register new user |
| POST | `/api/users/login` | ❌ | Login → JWT token |
| GET | `/api/categories` | ✅ | List categories (cached) |
| POST | `/api/categories` | ✅ | Create category |
| PUT | `/api/categories/{id}` | ✅ | Update category |
| DELETE | `/api/categories/{id}` | ✅ | Delete + all transactions |
| GET | `/api/categories/{id}/transactions` | ✅ | List transactions (cached) |
| POST | `/api/categories/{id}/transactions` | ✅ | Add transaction |
| PUT | `/api/categories/{id}/transactions/{tid}` | ✅ | Update transaction |
| DELETE | `/api/categories/{id}/transactions/{tid}` | ✅ | Delete transaction |

Full interactive docs: `/api-docs-ui`

---

## 🏗️ Architecture

```
┌─────────────────┐     HTTPS      ┌──────────────────────┐
│  Vercel         │ ─────────────▶ │  Railway             │
│  React/TS       │                │  Spring Boot 3.5     │
│  (Static CDN)   │                │  Port: $PORT (auto)  │
└─────────────────┘                └──────┬───────────────┘
                                          │
                              ┌───────────┴───────────┐
                              │                       │
                    ┌─────────▼────────┐   ┌──────────▼────────┐
                    │  Railway         │   │  Railway           │
                    │  PostgreSQL 16   │   │  Redis 7           │
                    └──────────────────┘   └───────────────────┘
```
