package com.example.finalprojectweatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalprojectweatherapp.data.HourlyData
import com.example.finalprojectweatherapp.data.WeatherData
import com.example.finalprojectweatherapp.utilities.convertToFahrenheit
import com.example.finalprojectweatherapp.utilities.describeWeatherCode
import com.example.finalprojectweatherapp.utilities.formatSunTime
import com.example.finalprojectweatherapp.utilities.getWeatherEmoji
import com.example.finalprojectweatherapp.utilities.getWindDirection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

private data class WeatherDetailsItem(val title: String, val value: String)

@Composable
fun DetailScreen(
    dayIndex : Int,
    weatherData : WeatherData,
    locationTitle : String,
    isCelsius : Boolean,
    isRefreshing : Boolean,
    lastUpdatedTime: String,
    onToggleUnit: () -> Unit,
    onRefresh: () -> Unit,
    onBackClick : () -> Unit,
    onLocationClick: () -> Unit
) {

    val rawDate = weatherData.dailyData.dates[dayIndex]
    val formattedDate = try {
        val date = LocalDate.parse(rawDate)
        date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault()))
    } catch (e: Exception) {
        "Unknown Date"
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            WeatherTopBar(
                locationName = locationTitle,
                locationSubtitle = formattedDate,
                onLocationClick = onLocationClick,
                showBackButton = true,
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                onRefresh()
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
            ) {
                item {
                    WeatherUtilityRow(
                        lastUpdatedTime = lastUpdatedTime,
                        isCelsius = isCelsius,
                        onTemperatureUnitClick = {
                            onToggleUnit()
                        }
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier.height(80.dp)
                    )
                }
                item {
                    val rawHigh = weatherData.dailyData.maxTemps[dayIndex]
                    val rawLow = weatherData.dailyData.minTemps[dayIndex]

                    DetailedWeatherHeader(
                        dailyHigh = if (isCelsius) {
                            rawHigh
                        } else {
                            convertToFahrenheit(rawHigh)
                        },
                        dailyLow = if (isCelsius) {
                            rawLow
                        } else {
                            convertToFahrenheit(rawLow)
                        },
                        weatherCode = weatherData.dailyData.weatherCodes[dayIndex],
                        isCelsius = isCelsius
                    )
                }
                item {
                    DetailHourlyWeatherForecast(
                        hourlyData = weatherData.hourlyData,
                        dayIndex = dayIndex,
                        isCelsius = isCelsius
                    )
                }
                item {
                    Spacer(
                        modifier = Modifier
                            .padding(top = 12.dp)
                    )
                }
                item {
                    val rawAppMax =
                        weatherData.dailyData.apparentMaxTemps.getOrNull(dayIndex) ?: 0.0
                    val rawAppMin =
                        weatherData.dailyData.apparentMinTemps.getOrNull(dayIndex) ?: 0.0

                    DetailedWeatherDetails(
                        apparentMaxTemps = if (isCelsius) rawAppMax else convertToFahrenheit(rawAppMax),
                        apparentMinTemps = if (isCelsius) rawAppMin else convertToFahrenheit(rawAppMin),
                        humidityMean = weatherData.dailyData.humidityMean.getOrNull(dayIndex)?: 0.0,
                        windSpeed = weatherData.dailyData.windSpeed.getOrNull(dayIndex)?: 0.0,
                        windDirection = weatherData.dailyData.windDirection.getOrNull(dayIndex)?: 0,
                        windGust = weatherData.dailyData.windGust.getOrNull(dayIndex)?: 0.0,
                        precipitation = weatherData.dailyData.precipitation.getOrNull(dayIndex)?: 0.0,
                        precipitationProbabilityMax = weatherData.dailyData.precipitationProbabilityMax.getOrNull(dayIndex)?: 0,
                        uvIndex = weatherData.dailyData.uvIndex.getOrNull(dayIndex)?: 0.0,
                        visibility = weatherData.dailyData.visibility.getOrNull(dayIndex)?: 0.0,
                        visibilityMin = weatherData.dailyData.visibilityMin.getOrNull(dayIndex)?: 0.0,
                        sunriseTime = weatherData.dailyData.sunriseTime.getOrNull(dayIndex)?: "",
                        sunsetTime = weatherData.dailyData.sunsetTime.getOrNull(dayIndex)?: "",
                        cloudCoverage = weatherData.dailyData.cloudCoverage.getOrNull(dayIndex)?: 0,
                        isCelsius = isCelsius
                    )
                }
            }
        }
    }
}

@Composable
fun DetailedWeatherHeader(
    dailyHigh : Double,
    dailyLow : Double,
    weatherCode : Int,
    isCelsius : Boolean
) {
    val roundedDailyHigh = dailyHigh.roundToInt()
    val roundedDailyLow = dailyLow.roundToInt()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.Top

            ) {
                Text(
                    text = "$roundedDailyHigh",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                    color = Color.White
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = if (isCelsius) {
                            "°C High"
                        } else {
                            "°F High"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier
                            .padding(top = 12.dp)
                    )
                    Text(
                        text = describeWeatherCode(weatherCode),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier
                            .padding(top = 12.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Low tonight: ${roundedDailyLow}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun DetailHourlyWeatherForecast(
    hourlyData : HourlyData,
    dayIndex : Int,
    isCelsius : Boolean
) {
    val startIndex = remember(dayIndex) {
        dayIndex * 24
    }

    val availableHoursAhead = hourlyData.hourlyTime.size - startIndex
    val displayLimit = minOf(availableHoursAhead, 24)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.25f)) // Matches your exact HomeScreen styling
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Hourly Forecast",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp) // Replicates your list spacing
        ) {
            items(displayLimit) { relativeIndex ->
                val actualIndex = startIndex + relativeIndex

                val rawTime = hourlyData.hourlyTime[actualIndex]
                val rawTemp = hourlyData.hourlyTemps[actualIndex]
                val weatherCode = hourlyData.hourlyWeatherCode[actualIndex]

                val formattedHour = try {
                    val parsedTime = LocalDateTime.parse(rawTime)
                    parsedTime.format(DateTimeFormatter.ofPattern("h a"))
                } catch (e: Exception) {
                    rawTime.substringAfter("T")
                }

                val currentTemp = if (isCelsius) {
                    rawTemp
                } else {
                    convertToFahrenheit(rawTemp)
                }
                val unitSuffix = if (isCelsius) {
                    "°C"
                } else {
                    "°F"
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${currentTemp.roundToInt()}$unitSuffix",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White
                    )
                    Text(
                        text = getWeatherEmoji(weatherCode),
                        fontSize = 24.sp
                    )
                    Text(
                        text = formattedHour,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.80f)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailedWeatherDetails(
    apparentMaxTemps : Double,
    apparentMinTemps : Double,
    humidityMean : Double,
    windSpeed : Double,
    windDirection : Int,
    windGust : Double,
    precipitation : Double,
    precipitationProbabilityMax : Int,
    uvIndex : Double,
    visibility : Double,
    visibilityMin : Double,
    sunriseTime: String,
    sunsetTime: String,
    cloudCoverage : Int,
    isCelsius: Boolean
) {
    val windDirectionLabel = getWindDirection(windDirection)

    val unitSuffix = if (isCelsius) "°C" else "°F"

    val feelsLikeRange = "${apparentMaxTemps.roundToInt()}° | ${apparentMinTemps.roundToInt()}$unitSuffix"
    val visibilityKm = "${(visibility / 1000)} km |  ${(visibilityMin / 1000)} km"

    val rows = listOf(
        Pair(
            WeatherDetailsItem("Feels Like Range", feelsLikeRange),
            WeatherDetailsItem("Max Wind Speed", "$windSpeed km/h $windDirectionLabel")
        ),
        Pair(
            WeatherDetailsItem("Wind Gust", "$windGust km/h"),
            WeatherDetailsItem("Cloud Coverage", "$cloudCoverage%")
        ),
        Pair(
            WeatherDetailsItem("Avg Humidity", "${humidityMean.roundToInt()}%"),
            WeatherDetailsItem("Visibility Range", visibilityKm)
        ),
        Pair(
            WeatherDetailsItem("UV Radiation", "$uvIndex"),
            WeatherDetailsItem("Precipitation", "$precipitation mm  |  $precipitationProbabilityMax% 🌧️")
        ),
        Pair(
            WeatherDetailsItem("Sunrise", formatSunTime(sunriseTime)),
            WeatherDetailsItem("Sunset", formatSunTime(sunsetTime))
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .padding(top = 16.dp, bottom = 12.dp)
    ) {
        Text(
            text = "Weather Details",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
        )
        rows.forEachIndexed { index, pair ->
            val isLastRow = index == rows.lastIndex
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (isLastRow) 0.dp else 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                DetailsItemCell(item = pair.first, modifier = Modifier.padding(start = 16.dp).weight(1f))
                DetailsItemCell(item = pair.second, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DetailsItemCell(item: WeatherDetailsItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.85f),
        )
        Text(
            text = item.value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}