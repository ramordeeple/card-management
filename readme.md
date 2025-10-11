# 🏦 **Bankcards — Card Management API**

> Сервис управления банковскими картами
> • **ADMIN:** выпуск / блокировка / активация карт
> • **USER:** просмотр и переводы между своими картами
> • JWT-аутентификация, фильтры, пагинация, Liquibase, Swagger

---

## ⚙️ **Стек технологий**

| Компонент       | Технологии                                                      |
| --------------- | --------------------------------------------------------------- |
| Backend         | Java 17+, Spring Boot 3, Spring Security (JWT), Spring Data JPA |
| Database        | PostgreSQL, Liquibase                                           |
| API Docs        | OpenAPI/Swagger (springdoc)                                     |
| Dev Environment | Docker Compose                                                  |
| Testing         | JUnit 5, Mockito, Testcontainers                                |
| Build           | Maven 3.9+                                                      |

---

## 🧱 **Архитектура проекта**

```
controller/    — REST-контроллеры (DTO ↔ сервисы)
service/       — бизнес-логика (переводы, статусы)
repository/    — Spring Data JPA
security/      — JWT, фильтры, UserDetailsService
exception/     — глобальный обработчик ошибок
dto/           — запросы/ответы API
resources/db/  — Liquibase changelogs
```

---

## 🚀 **Быстрый старт**

### 1️⃣ Предварительные требования

* ☕ Java 17+
* 🧱 Maven 3.9+
* 🐳 Docker & Docker Compose
* 💻 IntelliJ IDEA (рекомендуется)

---

### 2️⃣ Поднять базу данных (PostgreSQL)

В корне проекта:

```bash
docker compose up -d
```

Будут применены указанные ниже параметры значений:

| Параметр | Значение  |
| -------- | --------- |
| Host     | localhost |
| Port     | 5433      |
| Database | bankcards |
| Username | bank      |
| Password | bank      |

Либо же пкм по docker-compose.yml и `Run 'bank-cards-db'`

> ⚠️ Порт 5433 выбран, чтобы не конфликтовать с локальным 5432.

---

### 3️⃣ Настроить секреты (JWT / шифрование)

#### 💡 Настройка переменных в IntelliJ IDEA

1. Открой **Run → Edit Configurations...**
2. Выбери конфигурацию `BankCardsApplication`
3. В правом верхнем углу нажми **Modify options → Environment variables**
4. В появившейся секции кликните на иконку
5. Выберите уже готовый `.env.example` в корне проекта, как шаблон с **переменными средами**
6. Нажми **Apply → OK** и запусти на кнопку `Run`

> Теперь все переменные из `.env.example` будут автоматически подставляться в среду исполнения при запуске из IntelliJ IDEA.

---

### 4️⃣ Запуск приложения

**IntelliJ IDEA:** Run → `BankCardsApplication`

📍 Приложение доступно по адресу: [http://localhost:8080](http://localhost:8080)

---

## 📘 **API документация**

| Тип          | URL                                                                              |
| ------------ | -------------------------------------------------------------------------------- |
| Swagger UI   | [http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)             |
| OpenAPI JSON | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)           |
| OpenAPI YAML | [http://localhost:8080/v3/api-docs.yaml](http://localhost:8080/v3/api-docs.yaml) |

> 🔓 Swagger UI доступен без авторизации.

---

## 🗃️ **Liquibase (миграции)**

* Каталог: `src/main/resources/db/migration/`
* Точка входа: `changelog-master.yaml`
* Автозапуск при старте (`spring.liquibase.enabled=true`)

Таблицы после миграции: `users`, `roles`, `user_roles`, `cards`, `transfers`, `databasechangelog*`

> ⚠️ Запускается при старте приложения. Убедитесь, что в Docker Desktop появился и включен контейнер `bankcards`. 
---

## ⚙️ **Конфигурация (Примеры application.yml и .env.example)**
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

> 💡 При необходимости можно поменять ключи `SECURITY_JWT_SECRET` и `SECURITY_ENC_KEY` на нужные. 
> `DB_` значения берутся из `docker-compose.yml`.
---

## 🧪 **Тестирование**

### Через IntelliJ IDEA:

1. Открой проект в **IntelliJ IDEA**
2. Найдите папку с тестами: `src/test/java`
3. Кликните правой кнопкой на папку **java**
4. Выберите `Run 'Tests in 'java''`
5. Для интеграционных тестов убедитесь, что **Docker Desktop** с контейнером `bankcards` включен

### Покрытие:

* `TransferServiceImplTest` — логика переводов
* `CardAdminServiceImplTest` — операции администратора
* `CardControllerTest` — REST API MockMvc
* `SecurityIT` — доступ к эндпоинтам
* `ApplicationIT` — интеграционный тест

---

## 🌐 **HTTP Client (`api-samples.http`)**

📁 Файл: `requests/api-samples.http`

**Содержит сценарии, которые демонстрируют работу API:**

```http
### ─────────────────────────────
### GLOBAL VARS
### ─────────────────────────────
@baseUrl = http://localhost:8080
@adminUser = admin
@adminPass = admin123
@userUser = user
@userPass = user123
@adminToken = 
@userToken = 

### 1. AUTH: логин админом
POST {{baseUrl}}/api/v1/auth/login
Content-Type: application/json
{
  "username": "{{adminUser}}",
  "password": "{{adminPass}}"
}
> {% client.global.set("token", response.body.token); %}

### 2. AUTH: логин под юзера
POST {{baseUrl}}/api/v1/auth/login
Content-Type: application/json
{
  "username": "{{userUser}}",
  "password": "{{userPass}}"
}
> {% client.global.set("token", response.body.token); %}

### 3. AUTH: текущий пользователь
GET {{baseUrl}}/api/v1/auth/me
Authorization: Bearer {{userToken}}
Accept: application/json

### 4. ADMIN: создать карту пользователю
POST {{baseUrl}}/api/v1/admin/cards
Authorization: Bearer {{adminToken}}
Content-Type: application/json
Accept: application/json
{
  "number": "4211161311114242",
  "expiryMonth": 6,
  "expiryYear": 2032,
  "ownerId": 2
}

### 5. ADMIN: активировать карту
POST {{baseUrl}}/api/v1/admin/cards/5/activate
Authorization: Bearer {{adminToken}}

### 6. ADMIN: заблокировать карту
POST {{baseUrl}}/api/v1/admin/cards/5/block
Authorization: Bearer {{adminToken}}

### 7. ADMIN: удалить карту
DELETE {{baseUrl}}/api/v1/admin/cards/4
Authorization: Bearer {{adminToken}}

### 8. USER: список карт (фильтры + пагинация)
GET {{baseUrl}}/api/v1/cards
Authorization: Bearer {{userToken}}
Accept: application/json

### 9. USER: баланс конкретной карты
GET {{baseUrl}}/api/v1/cards/3/balance
Authorization: Bearer {{token}}
Accept: application/json

### 10. USER: запросить блокировку своей карты
POST {{baseUrl}}/api/v1/cards/3/block
Authorization: Bearer {{token}}

### 11. Пополнение карты
POST {{baseUrl}}/transfers/top-up
Authorization: Bearer {{userToken}}
Content-Type: application/json
Accept: application/json
{
  "cardId": 3,
  "amount": 250.00,
  "requestId": "rid-123456789"
}

### 12. USER: перевод между своими картами
POST {{baseUrl}}/transfers/own
Authorization: Bearer {{userToken}}
Content-Type: application/json
Accept: application/json
{
  "fromCardId": 3,
  "toCardId": 5,
  "amount": 30.00,
  "requestId": "{{lastTransferRequestId}}"
}
```

> 💡 Можно запускать напрямую из IntelliJ IDEA — глобальные переменные будут автоматически подставляться в запросы.

---





