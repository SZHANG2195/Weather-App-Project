# Android Weather App
Android weather app built using jetpack compose and MVVM architecture, using real time forecast and geocode data from the Open Meteo API.

## Getting Started
1. Clone the repo
2. Open in Android Studio (Hedgehog or later recommended)
3. Build and run on an emulator or physical device (min SDK 26)

## Screenshots
| Home Screen | Forecast View |
|---|---|
| <img width="270" height="600" alt="Screenshot_20260622_161518" src="https://github.com/user-attachments/assets/8e55e9a6-5666-42b8-8ee6-29d5ff75d317" /> | <img width="270" height="600" alt="Screenshot_20260622_161546" src="https://github.com/user-attachments/assets/9b029877-e737-4bf5-95e1-2fa89f0722c0" /> |

## Features
- Live weather forecasting from the Open Meteo API, pulldown to refresh data
- Search locations via geocoding also provided by the Open Meteo API
- Breakdown of the hourly and daily forecast for specific days
- Dynamic background gradient determined by the current weather at the location

## Tech Stack
- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM (ViewModel + Repository pattern)
- **Networking:** KTOR
- **API:** Open-Meteo (free, no key required)
- **Async:** Kotlin Coroutines + Flow

## Architecture

Follows MVVM with a clean separation between UI and data layers. 
The `ViewModel` exposes state via `StateFlow`, a `Repository` handles 
all data fetching logic, and Ktor manages HTTP requests to Open-Meteo 
and the geocoding endpoint. The UI layer observes state and recomposes 
reactively with no direct data access.
