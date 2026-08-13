# 🐱 Meow Moments

> 📱 Приложение с ежедневными фактами о кошках, которое помогает расслабиться и узнать что-то новое о пушистых друзьях.

---

## 📋 Содержание

- [Описание](#-описание)
- [Функции](#-функции)
- [Технологии](#-технологии)
- [Архитектура](#-архитектура)
- [Установка](#-установка)
- [Демонстрация](#-демонстрация)
- [Автор](#-автор)

---

## 📖 Описание

**Meow Moments** — это Android-приложение, разработанное в рамках дипломного проекта. Приложение предоставляет пользователям возможность:

- 🌞 Получать **ежедневный "факт дня"** о кошках.
- 📚 Просматривать **коллекцию собранных фактов**.
- ⭐ Отмечать понравившиеся факты как **избранные**.
- 👀 Просматривать **последние просмотренные** факты.
- 🔍 **Искать** факты по ключевым словам.
- 🌙 Переключаться между **светлой и темной темой**.
- 🔔 Получать **ежедневные push-уведомления** с новым фактом.

---

## ✨ Функции

| Функция | Описание |
|--------|----------|
| 🌞 **Факт дня** | Ежедневный уникальный факт о кошках на главном экране. |
| 📚 **Коллекция** | Все собранные пользователем факты. |
| ⭐ **Избранное** | Список фактов, отмеченных как любимые. |
| 👀 **Последние просмотренные** | Список недавно открытых фактов. |
| 🔍 **Поиск** | Поиск по тексту факта или категории. |
| 🌙 **Темная тема** | Поддержка светлой и темной темы (Material You). |
| 🔔 **Уведомления** | Ежедневные push-уведомления с "фактом дня". |
| 🧩 **Вкладки** | Объединённый экран "Мои факты" с вкладками. |
| 🔄 **Пагинация** | Загрузка фактов порционно для лучшей производительности. |
| 💾 **Кэширование** | Кэширование изображений и данных API. |
| 🌐 **Offline-first** | Работа приложения без интернета. |
| 🕐 **Фоновая синхронизация** | Автоматическое обновление данных в фоне. |

---

## 🛠 Технологии

- **Язык программирования**: [Kotlin](https://kotlinlang.org/)
- **Фреймворк**: [Android SDK](https://developer.android.com/)
- **Архитектура**: [MVVM](https://developer.android.com/topic/architecture) (Model-View-ViewModel)
- **База данных**: [Room](https://developer.android.com/training/data-storage/room)
- **Сетевые запросы**: [Retrofit](https://square.github.io/retrofit/), [OkHttp](https://square.github.io/okhttp/)
- **DI (Внедрение зависимостей)**: [Hilt](https://dagger.dev/hilt/)
- **Асинхронность**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html), [Flow](https://kotlinlang.org/docs/flow.html)
- **UI**: [RecyclerView](https://developer.android.com/jetpack/androidx/releases/recyclerview), [ConstraintLayout](https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout), [Material Components](https://github.com/material-components/material-components-android)
- **Пагинация**: [Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)
- **Кэширование изображений**: [Coil](https://coil-kt.github.io/coil/)
- **Фоновые задачи**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager)
- **Навигация**: [Navigation Component](https://developer.android.com/guide/navigation)

---

## 🏗 Архитектура

Проект построен по архитектуре **MVVM** с разделением на слои:


- **UI Layer**: Содержит `Activity`, `Fragment`, `ViewModel` и `ViewBinding`.
- **Domain Layer**: Содержит бизнес-логику (`UseCase`), доменные модели (`CatFact`) и интерфейсы репозитория.
- **Data Layer**: Содержит реализацию репозитория, работу с локальной базой данных (`Room`) и сетевыми запросами (`Retrofit`).

---

## 🚀 Установка

1. Клонируйте репозиторий:
   git clone https://github.com/ProstoRocker/meow-moments.git
2. Откройте проект в Android Studio.
3. Синхронизируйте проект (Gradle Sync).
4. Подключите устройство или запустите эмулятор.
5. Нажмите Run в Android Studio.

📺 Демонстрация

