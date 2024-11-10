# 📈 Crypto Tracker

Crypto Tracker is a native Android application that allows users to fetch and view information about available cryptocurrencies.

---

## 🗂️ App Architecture
Below is the app architecture used in Crypto Tracker:

![crypto-architecture-min](https://github.com/user-attachments/assets/9687da07-e2ba-49c5-9cb1-471d120d679d)

---

## 🏛️ UI

![Screenshot_20241110-160759-min](https://github.com/user-attachments/assets/78975f06-677b-4810-859d-a77a386034fe)

---

## 🏛️ Clean Architecture
The app follows Clean Architecture principles to ensure separation of concerns, maintainability, and testability.

### Layers
1. **Presentation Layer:** Contains UI components built with Jetpack Compose, and ViewModels for managing UI state.
2. **Domain Layer:** Contains business logic, including use cases and domain models.
3. **Data Layer:** Manages data sources (API and local database) and handles data operations.

---

## 🛠️ Tech Stack
- **Programming Language:** Kotlin
- **Architecture:** Clean Architecture (MVVM with UDF pattern)
- **Network:** Retrofit
- **Dependency Injection:** Hilt
- **Asynchronous Programming:** Coroutines and Flows
- **UI:** Jetpack Compose
- **Testing:** JUnit, Mockk
- **Data Storage:** Room Database
