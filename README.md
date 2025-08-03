# 🅿️ Car Parking Management System

**High-performance RESTful API for metropolitan parking operations with real-time availability, dynamic pricing, and
integrated payments.**

---

## 🏗️ Architecture

**Feature-based modular design:**

- **Facility** - Multi-level garages & street zones
- **Spot** - Grid-based allocation with floor organization
- **Pricing** - Dynamic rates (time, demand, weather, vehicle type, floor discounts)
- **Payment** - Card & mobile money integration
- **Session** - Vehicle parking with constraint enforcement
- **User/Vehicle** - Account & vehicle management

---

## ✨ Key Features

- Real-time parking availability
- Dynamic pricing with weather/event multipliers
- Floor-based discounts (higher = cheaper)
- Vehicle type pricing (motorcycle discounts, truck surcharges)
- Payment integration (VISA, Mastercard, Mobile Money)
- One active session per vehicle constraint
- Comprehensive audit logging & caching

---

## 🛠️ Tech Stack

| Component    | Technology                 |
|--------------|----------------------------|
| **Backend**  | Spring Boot 3.x + Java 21  |
| **Database** | PostgreSQL + JPA/Hibernate |
| **Security** | Keycloak OAuth2/JWT        |
| **Caching**  | Caffeine                   |
| **Docs**     | OpenAPI 3.0 (Swagger)      |
| **Frontend** | Next.js 15 + TypeScript    |
| **UI**       | Tailwind CSS + shadcn/ui   |
| **State**    | React Query                |

---

## 🌐 Demo Access

### Frontend App

**🔗 URL:** https://parking.quenchx.com/  
**👤 Credentials:** `herman` / `@hpaul99`

### API Documentation

**🔗 Swagger:** https://parking-api.quenchx.com/api/swagger-ui/index.html

---

## 🔑 API Testing

### 1. Get Access Token

```bash
curl --silent --location 'https://auth.quenchx.com/realms/parking/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=parking' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=herman' \
--data-urlencode 'password=@hpaul99' \
--data-urlencode 'client_secret=NNm5Vcne0caAXZFimtkQgh3sX4GzH9uK' \
| jq -r '.access_token'
```

### 2. Authorize in Swagger

Use token in Authorization header: `Bearer <your_access_token>`

---

## ⚙️ Local Environment

```bash
# Database
DB_HOST=parking-db
DB_PORT=5432
DB_NAME=parking
DB_USERNAME=parking
DB_PASSWORD=parking123

# Keycloak
KEYCLOAK_SERVER_URL=https://auth.quenchx.com
KEYCLOAK_REALM=parking
ADMIN_CLI_CLIENT_SECRET=bUbFl56OvDJre08ARhZ8w9PrSQR4F8QV
```