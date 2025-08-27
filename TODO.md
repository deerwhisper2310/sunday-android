Phase 1: Project Foundation & Core Services
- [x] **1. Set Up Android Studio Project**
  - Requirements:
    - Create a new project in Android Studio using the "Empty Activity" template with Jetpack Compose.
    - Set the package name (e.g., io.block.goose.sunday).
    - Configure build.gradle.kts to include dependencies:
      - Jetpack Compose
      - Retrofit, OkHttp, Kotlinx Serialization Converter
      - Room
      - Google's Fused Location Provider
      - Kotlin Coroutines and Flow
    - Fulfilled when: Project builds successfully and a basic "Hello World" Compose screen is visible.

- [x] **2. Implement Location Services**
  - Requirements:
    - Create a LocationService class to manage location fetching.
    - Integrate the Fused Location Provider API.
    - Implement permission handling for ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION.
    - Expose location as a StateFlow.
  - Fulfilled when: App requests permissions and provides current latitude/longitude reliably.

- [x] **3. Implement Networking Layer**
  - Requirements:
    - Define Kotlin data classes matching Open-Meteo UV API response.
    - Create Retrofit ApiService for UV forecast fetch.
    - Implement UvDataRepository to call ApiService and handle errors.
  - Fulfilled when: UvDataRepository returns parsed UV data for given coordinates.

Phase 2: Business Logic & Data Persistence
- [x] **4. Port Core Calculation Logic**
  - Requirements:
    - Port VitaminDCalculator.swift to VitaminDCalculator.kt, preserving methods/formulas.
    - Include factors: skin type, clothing, sunscreen, etc.
  - Fulfilled when: Kotlin class passes unit tests matching Swift outputs.

- [x] **5. Implement Data Persistence with Room**
  - Requirements:
    - Define UserPreferences entity (skin type, age, clothing/sunscreen defaults).
    - Define UvDataCache entity for cached weather data.
    - Create DAO interface and Room Database class.
  - Fulfilled when: App persists user settings and cached data across restarts.

Phase 3: User Interface & Experience
- [x] **6. Build the Main UI with Jetpack Compose**
  - Requirements:
    - Create MainViewModel integrating LocationService, UvDataRepository, and VitaminDCalculator.
    - Design MainScreen.kt in Compose observing ViewModel state.
    - Replicate UI components from iOS app:
      - Dynamic time-of-day background
      - Real-time UV index and Vitamin D production displays
      - Interactive pickers for clothing and sunscreen
      - "Burn limit" notification text
  - Fulfilled when: UI closely matches iOS app, displays real-time data, responds to user input.

- [x] **7. Integrations**
  - Requirements:
    - (Intentionally left blank for future consideration)
  - Fulfilled when: N/A.

Phase 4: Final Features & Polish
- [x] **8. Implement Notifications**
  - Requirements:
    - Create notification channels for alert types.
    - Use AlarmManager or WorkManager to schedule daily notifications for sunrise, sunset, and peak UV times.
  - Fulfilled when: App reliably delivers these three notifications at correct local times.

- [ ] **9. Final Testing and Refinement**
  - Requirements:
    - End-to-end testing on physical Android device.
    - Test features: location, API calls, calculations, UI interactions, database persistence, notifications.
    - Profile for performance and battery usage; fix bugs and polish UI/UX.
  - Fulfilled when: App is stable, feature-complete, and UX matches original iOS app.

Nice-to-have Backlog
- Home screen widget using Glance:
  - Create SundayWidgetReceiver and SundayWidget class.
  - Widget UI to show current UV index and Vitamin D status.
  - WorkManager job to update widget periodically.
  - Acceptance: Widget added to home screen and updates accurately.