package com.example.finalprojectweatherapp.data

import com.example.finalprojectweatherapp.utilities.presetLocations
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class WeatherRepository {
    suspend fun fetchWeatherData(latitude : Double, longitude : Double) : Result<WeatherData> {
        return try {
            val response = KTORClient.client.get("https://api.open-meteo.com/v1/forecast") {
                url {
                    parameter("latitude", latitude)
                    parameter("longitude", longitude)
                    parameter("daily", "temperature_2m_max,temperature_2m_min,apparent_temperature_max,apparent_temperature_min,weather_code,wind_speed_10m_max,wind_direction_10m_dominant,wind_gusts_10m_max,uv_index_max,precipitation_sum,sunset,sunrise,visibility_max,visibility_min,relative_humidity_2m_mean,precipitation_probability_max,cloud_cover_mean")
                    parameter("hourly", "temperature_2m,weather_code,uv_index,visibility,precipitation_probability")
                    parameter("current", "temperature_2m,relative_humidity_2m,is_day,apparent_temperature,wind_speed_10m,wind_direction_10m,weather_code,precipitation")
                    parameter("timezone", "auto")
                    parameter("forecast_days", 7)
                }
            }

            if(response.status == HttpStatusCode.OK) {
                Result.success(response.body<WeatherData>())
            } else {
                Result.failure(Exception("Error: ${response.status}"))
            }
        } catch (e : Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchCurrentCityData() : Result<Map<String, CurrentCityWeatherData>> {
        return try {
            val latitudes = presetLocations.joinToString(",") {
                it.latitude.toString()
            }
            val longitudes = presetLocations.joinToString(",") {
                it.longitude.toString()
            }

            val response = KTORClient.client.get("https://api.open-meteo.com/v1/forecast") {
                url {
                    parameter("latitude", latitudes)
                    parameter("longitude", longitudes)
                    parameter("current", "temperature_2m,weather_code,is_day")
                    parameter("timezone", "auto")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val results = response.body<List<CurrentCityData>>()
                val mappedResults = presetLocations.mapIndexed { index, preset ->
                    preset.locationName to results[index].currentData
                } .toMap()
                Result.success(mappedResults)
            } else {
                Result.failure(Exception("Error: ${response.status}"))
            }
        } catch (e : Exception) {
            Result.failure(e)
        }

    }

    suspend fun fetchGeocodeData(searchQuery : String) : Result<List<GeocodeResponseData>> {
        return try {
            val response = KTORClient.client.get("https://geocoding-api.open-meteo.com/v1/search") {
                url {
                    parameter("name", searchQuery)
                    parameter("count", 10)
                    parameter("language", "en")
                }
            }

            if (response.status == HttpStatusCode.OK) {
                val geocodeResponseData = response.body<GeocodeData>()
                Result.success(geocodeResponseData.geocodeResults ?: emptyList())
            } else {
                Result.failure(Exception("Error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
