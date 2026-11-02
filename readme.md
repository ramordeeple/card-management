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

---

## Local Monitoring: Prometheus and Grafana

The following steps use `PROMETHEUS_PORT=9090` and `GRAFANA_PORT=3000` from `.env`. If you change these values, use the corresponding ports in your browser.

### 1. Start the application and monitoring services

Run this command from the project root, where `docker-compose.yml` is located:

```bash
docker compose up -d --build
```

Wait for the application to start. Prometheus collects its metrics every 2 seconds.

### 2. Open Prometheus and check the metrics

1. Open [http://localhost:9090](http://localhost:9090). No login is required.
2. Open **Status → Targets**, or go directly to [http://localhost:9090/targets](http://localhost:9090/targets).
3. Find the `spring-actuator` target. Its endpoint is `http://app:8080/actuator/prometheus`, and its state should be **UP**.
4. Return to the query page, enter the following expression, and click **Execute**:

   ```promql
   up{job="spring-actuator"}
   ```

   The result should be `1`, which means Prometheus can collect metrics from the application.

5. To view JVM heap usage, replace the expression with the following query, click **Execute**, and select **Graph**:

   ```promql
   jvm_memory_used_bytes{application="bank-cards-service",area="heap"}
   ```

   Select a recent time range, such as the last 15 minutes. After a fresh start, allow a few scrapes for points to appear.

### 3. Open Grafana and connect Prometheus

1. Open [http://localhost:3000](http://localhost:3000).
2. Log in with username `admin` and password `admin`. If you have already changed the password, use your current one.
3. Complete the initial password prompt if it appears.
4. Open **Connections → Data sources → Add new data source** and select **Prometheus**.
5. Enter these settings:

   | Field | Value |
   | --- | --- |
   | Name | `prometheus` |
   | Prometheus server URL | `http://prometheus:9090` |
   | Scrape interval, under Interval behavior | `2s` |

6. Enable **Default** so this source is selected for new queries.
7. Click **Save & test**. The expected message is **Successfully queried the Prometheus API**.

Grafana connects to Prometheus through the Docker Compose service name `prometheus`. Inside the Grafana container, `localhost` refers to Grafana itself. Use `http://prometheus:9090` in the data source settings and `http://localhost:9090` in your browser.

If the `prometheus` data source already exists, open it and check these settings instead of creating another one.

### 4. Import and open the JVM dashboard

1. Open **Dashboards → New → Import**.
2. In the field for a Grafana.com dashboard URL or ID, enter `4701` and click **Load**. This imports the [JVM (Micrometer) dashboard](https://grafana.com/grafana/dashboards/4701-jvm-micrometer/).
3. When Grafana asks you to choose a Prometheus data source, select `prometheus`.
4. Click **Import**.
5. At the top of the dashboard, select these filters:

   | Filter | Value |
   | --- | --- |
   | Application | `bank-cards-service` |
   | Instance | `app:8080` |

6. Set the time range to **Last 15 minutes** and the refresh interval to **5s**.
7. Click **Refresh**. JVM memory, CPU, threads, and uptime panels should now show data.
8. Save the dashboard. To reopen it later, go to **Dashboards** and select **JVM (Micrometer)**.

### 5. Troubleshoot empty panels

If the dashboard shows **No data**, first check that the Prometheus target is **UP**, **Save & test** succeeds in Grafana, and both dashboard filters match the values above.

To check a query directly in Grafana, open **Explore**, select the `prometheus` data source, switch the query editor to **Code**, enter the following expression, and click **Run query**:

```promql
jvm_memory_used_bytes{application="bank-cards-service",instance="app:8080"}
```

If Explore returns data but the imported panels remain empty, check for unresolved data source references. This can happen when an older dashboard template is imported into a newer Grafana version:

1. Open **Connections → Data sources → prometheus** and copy the data source UID from the page URL. For example, in `/connections/datasources/edit/abc123`, the UID is `abc123`.
2. Return to the dashboard and click **Edit → Dashboard options → Settings** (or **View all settings**) → **JSON Model**.
3. Search the JSON for the literal placeholder `${DS_PROMETHEUS}`. If it exists, replace every occurrence with the UID from step 1, keeping the surrounding JSON quotes. This includes references inside both panels and their query targets.
4. Click **Save changes**, save the dashboard if prompted, and exit edit mode.
5. Select **Application = bank-cards-service**, **Instance = app:8080**, and **Last 15 minutes**, then refresh the dashboard.

Basic JVM graphs appear while the application is running. HTTP panels need requests to the application, and some optional panels in the template require additional metrics that this project does not expose.

The current Compose configuration stores Grafana settings inside its container. If that container is removed and recreated, repeat the data source and dashboard setup.

For more details, see the Grafana documentation for [Prometheus data sources](https://grafana.com/docs/grafana/latest/datasources/prometheus/configure/), [dashboard imports](https://grafana.com/docs/grafana/latest/visualizations/dashboards/build-dashboards/import-dashboards/), and [editing the dashboard JSON model](https://grafana.com/docs/grafana/latest/visualizations/dashboards/build-dashboards/view-dashboard-json-model/).






