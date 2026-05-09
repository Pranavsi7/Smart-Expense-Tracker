#  Smart Expense Tracker 

> Spring Boot 3.5 · PostgreSQL · Redis · Swagger · Docker · React/TypeScript · Mockito

[![CI](https://github.com/YOUR_USERNAME/expense-tracker-v2/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/expense-tracker-v2/actions)

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

---

## ☁️ Deploy to Railway (Backend) + Vercel (Frontend)

### Step 1 — Push to GitHub
```bash
git init
git add .
git commit -m "feat: expense tracker v2 full-stack"
git remote add origin https://github.com/YOUR_USERNAME/expense-tracker-v2.git
git push -u origin main
```

### Step 2 — Deploy Backend on Railway

1. Go to [railway.app](https://railway.app) → New Project → Deploy from GitHub repo
2. Select your repo → set **Root Directory** to `backend`
3. Add **PostgreSQL** plugin → Railway auto-sets `DATABASE_URL`
4. Add **Redis** plugin → Railway auto-sets `REDIS_URL`
5. Under **Variables**, add:

| Variable | Value |
|---|---|
| `DB_HOST` | from Railway PostgreSQL plugin |
| `DB_PORT` | `5432` |
| `DB_NAME` | from Railway PostgreSQL plugin |
| `DB_USER` | from Railway PostgreSQL plugin |
| `DB_PASSWORD` | from Railway PostgreSQL plugin |
| `REDIS_HOST` | from Railway Redis plugin |
| `REDIS_PORT` | `6379` |
| `REDIS_PASSWORD` | from Railway Redis plugin |
| `JWT_SECRET` | run `openssl rand -base64 64` |
| `JWT_ACCESS_TOKEN_EXPIRATION` | `900000` |
| `ALLOWED_ORIGIN` | `https://your-app.vercel.app` (set after Vercel deploy) |

6. Railway will build using `backend/Dockerfile` and deploy automatically.
7. Note your Railway URL: `https://expense-tracker-api-xxxx.up.railway.app`

> **Init DB:** Go to Railway PostgreSQL → Data tab → run the contents of `backend/init.sql`

### Step 3 — Deploy Frontend on Vercel

1. Go to [vercel.com](https://vercel.com) → New Project → Import your GitHub repo
2. Set **Root Directory** to `frontend`
3. Framework preset: **Vite** (auto-detected)
4. Build command: `npm run build` · Output dir: `dist`
5. Under **Environment Variables**, add:

| Variable | Value |
|---|---|
| `VITE_API_URL` | `https://expense-tracker-api-xxxx.up.railway.app` |

6. Click Deploy.
7. Copy your Vercel URL (e.g. `https://expense-tracker.vercel.app`)

### Step 4 — Final CORS Update

Go back to Railway → your backend service → Variables:
```
ALLOWED_ORIGIN = https://expense-tracker.vercel.app
```
Railway will auto-redeploy. Done — your app is live!

---

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
