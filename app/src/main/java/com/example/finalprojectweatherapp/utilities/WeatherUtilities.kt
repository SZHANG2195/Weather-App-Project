package com.example.finalprojectweatherapp.utilities

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


data class PresetLocation(
    val locationName: String,
    val latitude : Double,
    val longitude : Double,
    val admin1: String? = null,
    val country: String? = null
)

val presetLocations = listOf(
    PresetLocation("New York", 40.7143, -74.0060, "New York", "United States"),
    PresetLocation("Los Angeles", 34.0522, -118.2437, "California", "United States"),
    PresetLocation("London", 51.5074, -0.1278, "England", "United Kingdom"),
    PresetLocation("Paris", 48.8566, 2.3522, "Île-de-France Region", "France"),
    PresetLocation("Tokyo", 35.6762, 139.6503, "Tokyo", "Japan"),
    PresetLocation("Beijing", 39.9042, 116.4074, "Beijing Municipality", "China"),
    PresetLocation("Dubai", 25.2048, 55.2708, "Dubai", "United Arab Emirates"),
    PresetLocation("Singapore", 1.3521, 103.8198, null, "Singapore"),
    PresetLocation("Sydney", -33.8688, 151.2093, "New South Wales", "Australia"),
    PresetLocation("Mumbai", 19.0760, 72.8777, "Maharashtra", "India"),
    PresetLocation("São Paulo", -23.5505, -46.6333, "São Paulo", "Brazil"),
    PresetLocation("Cairo", 30.0444, 31.2357, "Cairo Governorate", "Egypt"),
    PresetLocation("Moscow", 55.7558, 37.6173, "Moscow", "Russia"),
    PresetLocation("Istanbul", 41.0082, 28.9784, "Istanbul", "Republic of Türkiye"),
    PresetLocation("Seoul", 37.5665, 126.9780, "Seoul", "South Korea"),
    PresetLocation("Mexico City", 19.4326, -99.1332, "Mexico City", "Mexico"),
    PresetLocation("Lagos", 6.5244, 3.3792, "Lagos", "Nigeria"),
    PresetLocation("Berlin", 52.5200, 13.4050, "State of Berlin", "Germany"),
    PresetLocation("Toronto", 43.6532, -79.3832, "Ontario", "Canada"),
    PresetLocation("Hong Kong", 22.3193, 114.1694, null, null)
)

fun describeWeatherCode(code : Int) : String {
    return when(code) {
        0 -> "Clear Sky"
        1 -> "Mainly Clear"
        2 -> "Partly Cloudy"
        3 -> "Overcast"
        45 -> "Foggy"
        48 -> "Icy Fog"
        51 -> "Light Drizzle"
        53 -> "Moderate Drizzle"
        55 -> "Heavy Drizzle"
        56 -> "Light Freezing Drizzle"
        57 -> "Heavy Freezing Drizzle"
        61 -> "Slight Rain"
        63 -> "Moderate Rain"
        65 -> "Heavy Rain"
        66 -> "Light Freezing Rain"
        67 -> "Heavy Freezing Rain"
        71 -> "Slight Snow"
        73 -> "Moderate Snow"
        75 -> "Heavy Snow"
        77 -> "Snow Grains"
        80 -> "Slight Showers"
        81 -> "Moderate Showers"
        82 -> "Heavy Showers"
        85 -> "Slight Snow Showers"
        86 -> "Heavy Snow Showers"
        95 -> "Thunderstorm"
        96 -> "Thunderstorm with Slight Hail"
        99 -> "Thunderstorm with Heavy Hail"
        else -> "Unknown Weather Condition"
    }
}

fun getWeatherEmoji(code : Int) : String {
    return when(code) {
        0 -> "☀️"
        1 -> "🌤️"
        2 -> "⛅"
        3 -> "☁️"
        45 -> "🌫️"
        48 -> "🌫️❄️"
        51, 53, 55 -> "🌦️"
        56, 57 -> "🌧️❄️"
        61, 63, 65 -> "🌧️"
        66, 67 -> "🌧️❄️"
        71, 73, 75 -> "❄️"
        77 -> "🌨️"
        80, 81, 82 -> "🌩️"
        85, 86 -> "🌨️"
        95 -> "⛈️"
        96, 99 -> "⛈️🌨️"
        else -> "🌡️"
    }
}

fun getWindDirection(degrees : Int) : String {
    return when (degrees) {
        in 338..360, in 0..22 -> "N"
        in 23..67 -> "NE"
        in 68..112 -> "E"
        in 113..157 -> "SE"
        in 158..202 -> "S"
        in 203..247 -> "SW"
        in 248..292 -> "W"
        in 293..337 -> "NW"
        else -> "N"
    }
}

fun getWeatherGradient(isDay: Int, weatherCode: Int) : Brush {
    return when {
        weatherCode in listOf(61, 63, 65, 66, 67, 80, 81, 82) -> Brush.verticalGradient(
            colors = listOf(Color(0xFF546E7A), Color(0xFF78909C))
        )
        weatherCode in listOf(71, 73, 75, 77, 85, 86) -> Brush.verticalGradient(
            colors = listOf(Color(0xFFB0BEC5), Color(0xFFCED6DB), Color(0xFFECEFF1))
        )
        weatherCode in listOf(95, 96, 99) -> Brush.verticalGradient(
            colors = listOf(Color(0xFF1A1A2E), Color(0xFF37474F))
        )
        weatherCode in listOf(45, 48) -> Brush.verticalGradient(
            colors = listOf(Color(0xFF9E9E9E), Color(0xFFEEEEEE))
        )
        isDay == 1 -> Brush.verticalGradient(
            colors = listOf(Color(0xFF82C8E5), Color(0xFF059CFC))
        )
        else -> Brush.verticalGradient(
            colors = listOf(Color(0xFF070B34), Color(0xFF483475))
        )
    }
}


fun convertToFahrenheit(temperatureCelsius : Double) : Double {
    return (temperatureCelsius * 9 / 5) + 32
}

fun formatSunTime(rawTime: String): String {
    return try {
        val parsedTime = LocalDateTime.parse(rawTime)
        parsedTime.format(DateTimeFormatter.ofPattern("h:mm a"))
    } catch (e: Exception) {
        if (rawTime.contains("T")) rawTime.substringAfter("T") else rawTime
    }
}