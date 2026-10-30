# Hotel Service

RESTful API для работы с отелями.

**Стек:** Java 21, Maven, Spring Boot 4, Spring Data JPA, Liquibase, H2, springdoc-openapi (Swagger UI).

## Запуск

Нужны JDK 21 и Maven.

```bash
mvn spring-boot:run
```

Приложение запускается на порту `8092`, у всех методов общий префикс `/property-view`.

## API

| Метод | URL | Описание |
|---|---|---|
| GET | `/property-view/hotels` | Список всех отелей с краткой информацией |
| GET | `/property-view/hotels/{id}` | Расширенная информация по отелю |
| GET | `/property-view/search` | Поиск по параметрам `name`, `brand`, `city`, `country`, `amenities` |
| POST | `/property-view/hotels` | Создание отеля |
| POST | `/property-view/hotels/{id}/amenities` | Добавление списка amenities к отелю |
| GET | `/property-view/histogram/{param}` | Количество отелей по `brand`, `city`, `country` или `amenities` |

Поиск: все параметры необязательные и объединяются через «И», регистр не учитывается.
`name` ищется по вхождению, остальные параметры — по точному совпадению.
`amenities` можно передать несколько раз (`?amenities=Free WiFi&amenities=Free parking`) — тогда у отеля должны быть все перечисленные.

## Документация и консоль БД

- Swagger UI: http://localhost:8092/property-view/swagger-ui.html
- OpenAPI: http://localhost:8092/property-view/v3/api-docs
- H2 console: http://localhost:8092/property-view/h2-console (JDBC URL `jdbc:h2:mem:hotels`, пользователь `sa`, пароль пустой)

## Переключение на другую базу данных

Схема создаётся Liquibase и не привязана к конкретной СУБД, параметры подключения задаются переменными окружения.
Например, для PostgreSQL:

1. Добавить драйвер в `pom.xml`:

   ```xml
   <dependency>
       <groupId>org.postgresql</groupId>
       <artifactId>postgresql</artifactId>
       <scope>runtime</scope>
   </dependency>
   ```

2. Запустить с параметрами подключения:

   ```bash
   DB_URL=jdbc:postgresql://localhost:5432/hotels DB_USERNAME=postgres DB_PASSWORD=postgres mvn spring-boot:run
   ```

## Тесты

```bash
mvn test
```
