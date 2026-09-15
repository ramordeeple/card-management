# 🏦 **Card Management - Bankcards**

### Bank card management service.
* **ADMIN:** issue / block / activate cards
* **USER:** view cards and transfer funds between your own cards
* JWT authentication, filtering, pagination, Liquibase, Swagger

---

## ⚙️ **Technology Stack**

| Component       | Technologies                                                      |
| --------------- | --------------------------------------------------------------- |
| Backend         | Java 17+, Spring Boot 3, Spring Security (JWT), Spring Data JPA |
| Database        | PostgreSQL, Liquibase, Redis                                    |
| API Docs        | OpenAPI/Swagger (springdoc)                                     |
| Dev Environment | Docker Compose, Prometheus, Grafana                             |
| Testing         | JUnit 5, Mockito, Testcontainers                                |
| Build           | Maven 3.9+                                                      |

---

## 🧱 **Project Architecture**

```
controller/    — REST controllers (DTOs ↔ services)
service/       — business logic (transfers, statuses)
repository/    — Spring Data JPA
security/      — JWT, filters, UserDetailsService
exception/     — global error handler
dto/           — API requests/responses
resources/db/  — Liquibase changelogs
```

---

## 🚀 **Quick Start**

### 1️⃣ Prerequisites

* ☕ Java 17+ 
* 🧱 Maven 3.9+
* 🐳 Docker Desktop
* 💻 IntelliJ IDEA 

---

### 2️⃣ Start the Database (PostgreSQL)

From the project root:

```bash
docker compose up -d
```

The following settings will be used:

| Parameter | Value  |
| -------- | --------- |
| Host     | localhost |
| Port     | 5433      |
| Database | bankcards |
| Username | bank      |
| Password | bank      |

Alternatively, right-click `docker-compose.yml` and select `Run 'bank-cards-db'`

> ⚠️ Port 5433 is used to avoid conflicts with a local database on port 5432.

---

### 3️⃣ Configure Secrets (JWT / Encryption)

#### 💡 Configure Environment Variables in IntelliJ IDEA

1. Open **Run → Edit Configurations...**
2. Select the `BankCardsApplication` configuration
3. In the upper-right corner, click **Modify options → Environment variables**
4. Click the icon in the section that appears
5. Select the existing `.env.example` in the project root as the **environment variables** template
6. Click **Apply → OK**, then click `Run` to start the application

> 💡 All variables from `.env.example` will now be loaded automatically into the runtime environment when launching from IntelliJ IDEA.

---

### 4️⃣ Run the Application

**IntelliJ IDEA:** Run → `BankCardsApplication`

📍 The application is available at: [http://localhost:8080](http://localhost:8080)

> ⚠️ Database migrations will be applied. Make sure the `bankcards` container exists and is running in **Docker Desktop**.
> Two test users, `ADMIN` and `USER`, will also be created.

---

## 📘 **API Documentation**

| Type          | URL                                                                              |
| ------------ | -------------------------------------------------------------------------------- |
| Swagger UI   | [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)             |
| OpenAPI JSON | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)           |
| OpenAPI YAML | [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) |

> 🔓 Swagger UI is accessible without authentication.

---

## 🗃️ **Liquibase (Migrations)**

* Directory: `src/main/resources/db/migration/`
* Entry point: `changelog-master.yaml`
* Runs automatically on startup (`spring.liquibase.enabled=true`)

Tables after migration: `users`, `roles`, `user_roles`, `cards`, `transfers`, `databasechangelog*`

> ⚠️ Runs on application startup. Make sure the `bankcards` container exists and is running in **Docker Desktop**.
---

## ⚙️ **Configuration (application.yml and .env.example Examples)**
### 1) **application.yml**
```yaml
spring:
  config:
    import: optional:file:.env[.properties]

  datasource:
    url: ${DB_URL}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    hibernate:
      ddl-auto: validate

  liquibase:
    change-log: classpath:db/migration/changelog-master.yaml

security:
  jwt:
    secret: ${SECURITY_JWT_SECRET}
  enc:
    key: ${SECURITY_ENC_KEY}

logging:
  level:
    org.springframework.web: WARN
```

### 2) **.env.example**
```.env.example
SECURITY_JWT_SECRET=VGhpcy1pcy1hLXNlY3JldC1qd3QtYmFzZTY0LWtleS0xMjM0NTY3ODkwMTIzNA==
SECURITY_ENC_KEY=w3m7b3k5yqZN7i0Hq7n9pK6vX3iF2l7r5p8q2v6t0mQ=
DB_URL=jdbc:postgresql://localhost:5433/bankcards
DB_USERNAME=bank
DB_PASSWORD=bank
```

> 💡 You can replace the `SECURITY_JWT_SECRET` and `SECURITY_ENC_KEY` keys as needed.
> The `DB_` values come from `docker-compose.yml`.
---

## 🧪 **Testing**

### Using IntelliJ IDEA:

1. Open the project in **IntelliJ IDEA**
2. Locate the test directory: `src/test/java`
3. Right-click the **java** directory
4. Select `Run 'Tests in 'java''`
5. For integration tests, make sure **Docker Desktop** and the `bankcards` container are running

### Test Coverage:

* `TransferServiceImplTest` — transfer logic
* `CardAdminServiceImplTest` — administrator operations
* `CardControllerTest` — REST API MockMvc
* `SecurityIT` — endpoint access
* `ApplicationIT` — integration test

---

## 🌐 **HTTP Client (`api-samples.http`)**

By default, two test users, `USER`/`user123` and `ADMIN`/`admin123`, are created on application startup by `bootstrap/AdminBootstrap`

> 📁 The file `requests/api-samples.http`, relative to the project root, contains **API** usage examples

### 💳 **Scenarios**

#### 👨‍💼 ADMIN

- Creates a card for a user (`POST /api/v1/admin/cards`)
- The card number is stored in the **database** in **encrypted form**, using `SECURITY_ENC_KEY` for encryption.
- Can **activate**, **block**, and **delete** a card via `/api/v1/admin/cards/{id}/...`

#### 🙋‍♂️ USER

- Lists their own cards: `GET /api/v1/cards`
- Checks a card balance: `GET /api/v1/cards/{id}/balance`
- Can top up their own card balance (`POST /transfers/top-up`)
- Transfers funds between their own cards (`POST /transfers/own`); the system verifies ownership and balance, then creates a transfer history record
- Can request that their own card be blocked (`POST /api/v1/cards/{id}/block`)
> 💡 All actions require a valid user/administrator JWT token

---

## **⚡ Highload & Performance Optimization**

The project is designed to meet the requirements of modern high-load systems.

### **🧵 Java 21 Virtual Threads**

Unlike the classic "thread-per-request" model, where each thread consumes \~1 MB of memory, the application uses **virtual threads**:

* Handle thousands of concurrent requests without tying up system resources.
* Are well suited to I/O-intensive operations (database and Redis queries).
* Enable them with: spring.threads.virtual.enabled: true.

### **🚀 Redis Caching Strategy**

Caching is implemented to minimize the load on PostgreSQL:

* **Reading data:** Card details are cached in Redis (@Cacheable).
* **Invalidation:** Any change to a card’s state (blocking, activation, transfer) automatically evicts the cached data (@CacheEvict) to maintain consistency.
* **Result:** Response time decreases from \~20 ms to \<2 ms.

### **📊 Monitoring (Observability)**

A monitoring stack is deployed to analyze system health in real time:

* **Prometheus:** Collects application metrics (latency, throughput, error rate).
* **Grafana:** Visualizes metrics (dashboards for monitoring resources and business metrics).
* **Micrometer:** Instruments the code to export metrics in Prometheus format.

## **📈 Load Testing (Load Check)**

You can test system performance using the built-in endpoints and .http files.

### **Test Scenarios**

#### **1\. Verify Virtual Threads**

Verifies that requests are processed by Java 21 virtual threads.

\#\#\# Check thread info  
GET {{baseUrl}}/api/v1/cards/thread-test

*The response will contain the thread name: VirtualThread\[...\].*

#### **2\. Verify Redis Caching**

The first request queries the database; all subsequent requests read from Redis memory.

\#\#\# Get card details (populates/reads from cache)  
GET http://localhost:8080/api/v1/cards/1

#### **3\. Stress Test (Loadcheck)**

Generates a large volume of requests to test connection pool stability and cache performance.

\#\#\# Loadcheck (Simulate 100,000+ operations)  
POST http://localhost:8080/api/v1/cards/load-test/1

*During the test, the Grafana charts will show a spike in activity.*






