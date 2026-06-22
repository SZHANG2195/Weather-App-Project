package com.example.finalprojectweatherapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherData (
    @SerialName("daily") val dailyData : DailyData,
    @SerialName("current") val currentData : CurrentData,
    @SerialName("hourly") val hourlyData : HourlyData
)

@Serializable
data class DailyData (
    @SerialName("time") val dates : List<String>,
    @SerialName("temperature_2m_max") val maxTemps : List<Double>,
    @SerialName("temperature_2m_min") val minTemps : List<Double>,
    @SerialName("apparent_temperature_max") val apparentMaxTemps : List<Double>,
    @SerialName("apparent_temperature_min") val apparentMinTemps : List<Double>,
    @SerialName("weather_code") val weatherCodes : List<Int>,
    @SerialName("wind_speed_10m_max") val windSpeed : List<Double>,
    @SerialName("wind_direction_10m_dominant") val windDirection : List<Int>,
    @SerialName("wind_gusts_10m_max") val windGust : List<Double>,
    @SerialName("uv_index_max") val uvIndex : List<Double>,
    @SerialName("precipitation_sum") val precipitation : List<Double>,
    @SerialName("sunset") val sunsetTime : List<String>,
    @SerialName("sunrise") val sunriseTime : List<String>,
    @SerialName("visibility_max")  val visibility : List<Double>,
    @SerialName("visibility_min") val visibilityMin : List<Double>,
    @SerialName("relative_humidity_2m_mean") val humidityMean : List<Double>,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax : List<Int>,
    @SerialName("cloud_cover_mean") val cloudCoverage : List<Int>

)

@Serializable
data class CurrentData (
    @SerialName("time") val currentTime : String,
    @SerialName("temperature_2m") val currentTemp : Double,
    @SerialName("relative_humidity_2m") val currentHumidity : Int,
    @SerialName("is_day") val dayNight : Int,
    @SerialName("apparent_temperature") val feelsLike : Double,
    @SerialName("wind_speed_10m") val currentWindSpeed : Double,
    @SerialName("wind_direction_10m") val currentWindDirection : Int,
    @SerialName("weather_code") val currentWeatherCode : Int,
    @SerialName("precipitation") val currentPrecipitation : Double,
)

@Serializable
data class HourlyData (
    @SerialName("time") val hourlyTime : List<String>,
    @SerialName("temperature_2m") val hourlyTemps : List<Double>,
    @SerialName("weather_code") val hourlyWeatherCode : List<Int>,
    @SerialName("uv_index") val hourlyUVIndex : List<Double>,
    @SerialName("visibility") val hourlyVisibility : List<Double>,
    @SerialName("precipitation_probability") val hourlyPrecipitationChance : List<Int>
)

@Serializable
data class CurrentCityData (
    @SerialName("current") val currentData : CurrentCityWeatherData
)

@Serializable
data class CurrentCityWeatherData(
    @SerialName("temperature_2m") val currentTemp: Double,
    @SerialName("weather_code") val currentWeatherCode: Int,
    @SerialName("is_day") val dayNight: Int
)

@Serializable
data class GeocodeData(
    @SerialName("results") val geocodeResults : List<GeocodeResponseData>? = null
)

@Serializable
data class GeocodeResponseData(
    @SerialName("name") val cityName : String,
    @SerialName("latitude") val cityLatitude : Double,
    @SerialName("longitude") val cityLongitude : Double,
    @SerialName("country") val cityCountry : String? = null,
    @SerialName("admin1") val cityState : String? = null
)

