# Recipe API

## Описание

**Recipe API** — это микросервисное приложение для работы с рецептами. Оно позволяет пользователям получать рецепты, переводить их на разные языки, сохранять, редактировать и удалять рецепты, а также отправлять уведомления пользователям по электронной почте.

## Функционал

- Получение рецептов по названию или по ссылке.
- Добавление, удаление и редактирование собственных рецептов.
- Перевод рецептов на разные языки с помощью внешнего API.
- Отправка уведомлений на электронную почту при добавлении или изменении рецепта.
- Аутентификация и авторизация с использованием JWT.
- Асинхронная обработка запросов с использованием RabbitMQ.
- Кэширование данных с помощью Redis для оптимизации производительности.
- API Gateway для маршрутизации запросов между микросервисами.

## Стек технологий

- **Java 17**
- **Spring Boot**
- **MongoDB** — для хранения рецептов.
- **Redis** — для кэширования запросов.
- **RabbitMQ** — для обмена сообщениями между микросервисами.
- **API Gateway** — для управления запросами.
- **Spring Security + JWT** — для аутентификации и авторизации пользователей.
- **PostgreSQL** — для хранения информации о пользователях.
- **Spring Data JPA** — для работы с реляционными базами данных.
- **Docker** — для контейнеризации и развертывания микросервисов.
- **Mail API** — для отправки email-уведомлений пользователям.
- **Translation API** — для перевода рецептов на разные языки.

## Архитектура

Проект использует микросервисную архитектуру, состоящую из нескольких ключевых компонентов:

- **Recipe API** — основной сервис для управления рецептами (получение, создание, редактирование, удаление).
- **Translation API** — сервис для перевода рецептов с использованием внешнего API.
- **User API** — сервис для управления пользователями (регистрация, аутентификация, хранение рецептов).
- **Mail API** — сервис для отправки уведомлений на email.
- **API Gateway** — единая точка входа для всех запросов к микросервисам.

## Установка и запуск

### 1. Клонирование репозитория

```bash
git clone https://github.com/absolute11/recipe-api.git

2. Настройка конфигурации
Создайте файл application.properties для каждого микросервиса с необходимыми параметрами подключения (MongoDB, PostgreSQL, Redis, RabbitMQ и т.д.).

Пример конфигурации для Recipe API:

properties

spring.data.mongodb.uri=mongodb://localhost:27017/recipeDB
spring.redis.host=localhost
spring.rabbitmq.host=localhost
spring.mail.host=smtp.example.com
jwt.secret=your_secret_key
3. Запуск с Docker
Проект поддерживает контейнеризацию с использованием Docker. Для запуска всех сервисов выполните следующую команду:


docker-compose up
Это поднимет все микросервисы, базы данных и необходимые очереди сообщений.

4. Запуск вручную
Если вы не используете Docker, можно запустить каждый сервис по отдельности:



Примеры API запросов
Получение рецепта по названию

GET /api/recipes/search?name=pizza
Ответ:


{
  "id": "60c72b2f4f1a2563b4e8a56e",
  "title": "Pizza",
  "ingredients": ["Flour", "Tomato", "Cheese"],
  "steps": ["Mix ingredients", "Bake in oven"],
  "category": "Italian",
  "url": "https://example.com/pizza"
}
Получение рецепта по URL

GET /api/recipes?url=https://www.allrecipes.com/recipe/92878/no-bake-peanutty-graham-treats/
Ответ:


{
  "id": "60c72b2f4f1a2563b4e8a56e",
  "title": "No-Bake Peanutty Graham Treats",
  "ingredients": ["Graham crackers", "Peanut butter", "Chocolate"],
  "steps": ["Mix ingredients", "Refrigerate"],
  "category": "Dessert",
  "url": "https://example.com/no-bake-peanutty-graham-treats"
}

POST /api/recipes
Тело запроса:


{
  "title": "Pasta",
  "ingredients": ["Pasta", "Tomato", "Cheese"],
  "steps": ["Boil pasta", "Add sauce"],
  "category": "Italian"
}
Удаление рецепта

DELETE /api/recipes/66f2bee3fffea37a25842a2a
Ответ:


{
  "status": "Recipe deleted"
}
Логин

POST /api/v1/auth/login
Тело запроса:


{
  "email": "user@example.com",
  "password": "your_password"
}
Ответ:

{
  "token": "your_jwt_token"
}
Получение любимых рецептов с возможностью перевода

GET /api/recipes/favorites
Ответ:

[
  {
    "id": "60c72b2f4f1a2563b4e8a56e",
    "title": "Pizza",
    "ingredients": ["Flour", "Tomato", "Cheese"],
    "steps": ["Mix ingredients", "Bake in oven"],
    "category": "Italian",
    "url": "https://example.com/pizza"
  }
]
Перевод рецепта на любой язык
POST /api/recipes/translate
Тело запроса:

{
  "recipeId": "60c72b2f4f1a2563b4e8a56e",
  "targetLanguage": "fr"
}
Ответ:

{
  "id": "60c72b2f4f1a2563b4e8a56e",
  "title": "Pizza (French)",
  "ingredients": ["Farine", "Tomate", "Fromage"],
  "steps": ["Mélangez les ingrédients", "Cuire au four"],
  "category": "Italien",
  "url": "https://example.com/pizza"
}
