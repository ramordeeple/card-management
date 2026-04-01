# 🏦 **Card Management - Bankcards**

### Сервис управления банковскими картами.
* **ADMIN:** выпуск / блокировка / активация карт
* **USER:** просмотр и переводы между своими картами
* JWT-аутентификация, фильтры, пагинация, Liquibase, Swagger

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
* 🐳 Docker Desktop
* 💻 IntelliJ IDEA 

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

Либо же правой кнопкой мыши по `docker-compose.yml` и `Run 'bank-cards-db'`

> ⚠️ Порт 5433 выбран, чтобы не конфликтовать с локальным 5432.

---

### 3️⃣ Настройка секретов (JWT / шифрование)

#### 💡 Настройка переменных окружения в IntelliJ IDEA

1. Откройте **Run → Edit Configurations...**
2. Выберите конфигурацию `BankCardsApplication`
3. В правом верхнем углу нажмите **Modify options → Environment variables**
4. В появившейся секции кликните на иконку
5. Выберите уже готовый `.env.example` в корне проекта, как шаблон с **переменными средами**
6. Нажмите **Apply → OK** и запустите на кнопку `Run`

> 💡 Теперь все переменные из `.env.example` будут автоматически подставляться в среду исполнения при запуске из IntelliJ IDEA.

---

### 4️⃣ Запуск приложения

**IntelliJ IDEA:** Run → `BankCardsApplication`

📍 Приложение доступно по адресу: [http://localhost:8080](http://localhost:8080)

> ⚠️ Будут применены миграции. Убедитесь, что в **Docker Desktop** появился и включен контейнер `bankcards`,
> а также будут созданы два тестовых пользователя `ADMIN` и `USER`.

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

> ⚠️ Запускается при старте приложения. Убедитесь, что в **Docker Desktop** появился и включен контейнер `bankcards`. 
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

1. Откройте проект в **IntelliJ IDEA**
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

По умолчанию, после запуска приложения, создаются два проверочных пользоваться `USER`/`user123` и `ADMIN`/`admin123` в `bootstrap/AdminBootstrap`

> 📁 Файл в корне проекта:`requests/api-samples.http` имеет примеры работы **API**

### 💳 **Сценарии**

#### 👨‍💼 ADMIN

- Создаёт карту пользователю (`POST /api/v1/admin/cards`)
- Номер карты сохраняется в **базе данных** в **зашифрованном виде** — шифруется с помощью `SECURITY_ENC_KEY`.
- Может **активировать**, **заблокировать** и **удалить** карту через `/api/v1/admin/cards/{id}/...`

#### 🙋‍♂️ USER

- Просматривает список своих карт: `GET /api/v1/cards`
- Проверяет баланс карты: `GET /api/v1/cards/{id}/balance`
- Может пополнять баланс своей карты (`POST /transfers/top-up`)
- Делает переводы между своими картами (`POST /transfers/own`), при этом система проверяет владельца, баланс и создаёт запись в истории переводов
- Может запросить блокировку своей карты (`POST /api/v1/cards/{id}/block`)
> 💡 Все действия требуют валидного JWT токена пользователя/администратора

---

## **⚡ Highload & Performance Optimization**

Проект спроектирован с учетом требований к современным высоконагруженным системам (Highload).

### **🧵 Java 21 Virtual Threads**

В отличие от классической модели "thread-per-request", где каждый поток потребляет \~1МБ памяти, приложение использует **виртуальные потоки**:

* Позволяют обрабатывать тысячи конкурентных запросов без блокировки системных ресурсов.
* Идеально подходят для I/O-интенсивных операций (запросы к БД и Redis).
* Для активации используется: spring.threads.virtual.enabled: true.

### **🚀 Redis Caching Strategy**

Для минимизации нагрузки на PostgreSQL реализовано кэширование:

* **Чтение данных:** Детальная информация о картах кэшируется в Redis (@Cacheable).
* **Инвалидация:** При любом изменении состояния карты (блокировка, активация, перевод) кэш автоматически сбрасывается (@CacheEvict), обеспечивая консистентность.
* **Результат:** Время отклика сокращается с \~20мс до \<2мс.

### **📊 Monitoring (Observability)**

Для анализа состояния системы в режиме реального времени развернут стек мониторинга:

* **Prometheus:** Сбор метрик приложения (latency, throughput, error rate).
* **Grafana:** Визуализация метрик (дашборды для мониторинга ресурсов и бизнес-показателей).
* **Micrometer:** Инструментация кода для экспорта метрик в формате Prometheus.

## **📈 Нагрузочное тестирование (Load Check)**

Можно проверить производительность системы, используя встроенные эндпоинты и файлы .http.

### **Сценарии тестирования**

#### **1\. Проверка работы виртуальных потоков**

Позволяет убедиться, что запросы обрабатываются именно виртуальными потоками Java 21\.

\#\#\# Check thread info  
GET {{baseUrl}}/api/v1/cards/thread-test

*В ответе будет имя потока: VirtualThread\[...\].*

#### **2\. Проверка кэширования в Redis**

Первый запрос обратится к БД, все последующие — к оперативной памяти Redis.

\#\#\# Get card details (populates/reads from cache)  
GET http://localhost:8080/api/v1/cards/1

#### **3\. Стресс-тест (Loadcheck)**

Запуск массовой генерации запросов для проверки стабильности пула соединений и скорости работы кэша.

\#\#\# Loadcheck (Simulate 100,000+ operations)  
POST http://localhost:8080/api/v1/cards/load-test/1

*Во время теста будет всплеск активности на графиках в Grafana.*






