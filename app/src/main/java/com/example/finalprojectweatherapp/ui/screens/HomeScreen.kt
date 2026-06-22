package com.example.finalprojectweatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalprojectweatherapp.data.CurrentData
import com.example.finalprojectweatherapp.data.DailyData
import com.example.finalprojectweatherapp.data.HourlyData
import com.example.finalprojectweatherapp.ui.WeatherViewModel
import com.example.finalprojectweatherapp.utilities.convertToFahrenheit
import com.example.finalprojectweatherapp.utilities.describeWeatherCode
import com.example.finalprojectweatherapp.utilities.formatSunTime
import com.example.finalprojectweatherapp.utilities.getWeatherEmoji
import com.example.finalprojectweatherapp.utilities.getWindDirection
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

private data class CurrentWeatherDetailsItem(val title: String, val value: String)

@Composable
fun HomeScreen(
    viewModel : WeatherViewModel,
    onToggleUnit: () -> Unit,
    onRefresh: () -> Unit,
    onLocationClick : () -> Unit,
    onDayClick: (Int) -> Unit
) {
    val isCelsius by viewModel.isCelsius.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val isRefreshing = viewModel.isRefreshing
    val lastUpdatedTime = viewModel.lastUpdated
    val locationTitle = viewModel.selectedLocationName
    val locationSubtitle = viewModel.selectedLocationSubtitle

    WeatherStateWrapper(
        uiState = uiState,
        onRefresh = onRefresh
    ) { successState ->
        val weatherData = successState.data
        val currentData = weatherData.currentData
        val dailyData = weatherData.dailyData
        val hourlyData = weatherData.hourlyData

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                WeatherTopBar(
                    locationName = locationTitle,
                    locationSubtitle = locationSubtitle,
                    onLocationClick = onLocationClick,
                    showBackButton = false
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
                    .padding(paddingValues)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                ) {
                    item {
                        WeatherUtilityRow(lastUpdatedTime = lastUpdatedTime,
                            isCelsius = isCelsius,
                            onTemperatureUnitClick = { onToggleUnit()
                            }
                        )
                    }
                    item {
                        Spacer(
                            modifier = Modifier.height(80.dp)
                        )
                    }
                    item {
                        CurrentWeatherHeader(
                            currentTemp = if (isCelsius) {
                                currentData.currentTemp
                            } else {
                                convertToFahrenheit(currentData.currentTemp)
                            },
                            dailyHigh = if (isCelsius) {
                                dailyData.maxTemps[0]
                            } else {
                                convertToFahrenheit(dailyData.maxTemps[0])
                            },
                            dailyLow = if (isCelsius) {
                                dailyData.minTemps[0]
                            } else {
                                convertToFahrenheit(dailyData.minTemps[0])
                            },
                            weatherCode = currentData.currentWeatherCode,
                            isCelsius = isCelsius
                        )
                    }
                    item {
                        HourlyWeatherForecast(
                            hourlyData = hourlyData,
                            currentData = currentData,
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
                        DailyWeatherForecast(
                            dailyData = dailyData,
                            isCelsius = isCelsius,
                            onDayClick = { dayIndex ->
                                onDayClick(dayIndex)
                            }
                        )
                    }
                    item {
                        Spacer(
                            modifier = Modifier
                                .padding(top = 12.dp)
                        )
                    }
                    item {
                        val current = weatherData.currentData
                        val hourly = weatherData.hourlyData
                        val currentHourIndex = remember(hourly, current) {
                            val currentHourMatchString = current.currentTime.take(13)
                            val index = hourly.hourlyTime.indexOfFirst { it.startsWith(currentHourMatchString) }
                            if (index != -1) {
                                index
                            } else {
                                0
                            }
                        }

                        val hourlyVisibility = hourly.hourlyVisibility.getOrNull(currentHourIndex)?: 0.0
                        val hourlyUvIndex = hourly.hourlyUVIndex.getOrNull(currentHourIndex)?: 0.0
                        val hourlyPrecipitationChance = hourly.hourlyPrecipitationChance.getOrNull(currentHourIndex)?: 0

                        CurrentWeatherDetails(
                            feelsLike = if (isCelsius) {
                                currentData.currentTemp
                            } else {
                                convertToFahrenheit(currentData.currentTemp)
                            },
                            currentHumidity = currentData.currentHumidity,
                            currentWindSpeed = currentData.currentWindSpeed,
                            currentWindDirection = currentData.currentWindDirection,
                            currentPrecipitation = currentData.currentPrecipitation,
                            hourlyVisibility = hourlyVisibility,
                            hourlyUvIndex = hourlyUvIndex,
                            hourlyPrecipitationChance = hourlyPrecipitationChance,
                            sunriseTime = dailyData.sunriseTime[0],
                            sunsetTime = dailyData.sunsetTime[0],
                            isCelsius = isCelsius
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun CurrentWeatherHeader(
    currentTemp : Double,
    dailyHigh : Double,
    dailyLow : Double,
    weatherCode : Int,
    isCelsius : Boolean
) {
    val roundedCurrentTemp = currentTemp.roundToInt()
    val roundedDailyHigh = dailyHigh.roundToInt()
    val roundedDailyLow = dailyLow.roundToInt()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "$roundedCurrentTemp",
                    style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                    color = Color.White
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                ) {
                    Text(
                        text = if (isCelsius) {
                            "°C"
                        } else {
                            "°F"
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
                    text = "${roundedDailyHigh}°  |  ${roundedDailyLow}°",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Composable
fun HourlyWeatherForecast(
    hourlyData : HourlyData,
    currentData : CurrentData,
    isCelsius: Boolean
) {
    val startIndex = remember(hourlyData, currentData) {
        val currentHourMatchString = currentData.currentTime.take(13)
        val index = hourlyData.hourlyTime.indexOfFirst { hourlyTimeString ->
            hourlyTimeString.startsWith(currentHourMatchString)
        }
        if (index != -1) {
            index
        } else {
            0
        }
    }

    val availableHoursAhead = hourlyData.hourlyTime.size - startIndex
    val displayLimit = minOf(availableHoursAhead, 24)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "24 Hour Forecast",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 16.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
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
fun DailyWeatherForecast(
    dailyData : DailyData,
    isCelsius: Boolean,
    onDayClick : (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .padding(top = 16.dp, bottom = 8.dp)
    ) {
        Text(
            text = "7 Day Forecast",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 4.dp)
        )

        dailyData.dates.forEachIndexed { index, dateString ->
            val dayLabel = if (index == 0) {
                "Today"
            } else {
                try {
                    val parsedDate = LocalDate.parse(dateString)
                    parsedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                } catch (e: Exception) {
                    "Day ${index + 1}"
                }
            }

            val maxTempRaw = dailyData.maxTemps[index]
            val minTempRaw = dailyData.minTemps[index]
            val weatherCode = dailyData.weatherCodes[index]

            val displayHigh = if (isCelsius) {
                maxTempRaw
            } else {
                convertToFahrenheit(maxTempRaw)
            }
            val displayLow = if (isCelsius) {
                minTempRaw
            } else {
                convertToFahrenheit(minTempRaw)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onDayClick(index)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = dayLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1.5f)
                )
                Text(
                    text = getWeatherEmoji(weatherCode),
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1.5f)
                ) {
                    Text(
                        text = "${displayHigh.roundToInt()}°",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = " / ",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = "${displayLow.roundToInt()}°",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Text(
                        text = if (isCelsius) {
                            "C"
                        } else {
                            "F"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                    )
                }
            }
        }
    }
}

@Composable
fun CurrentWeatherDetails(
    feelsLike : Double,
    currentHumidity : Int,
    currentWindSpeed : Double,
    currentWindDirection : Int,
    currentPrecipitation : Double,
    hourlyVisibility : Double,
    hourlyUvIndex : Double,
    hourlyPrecipitationChance : Int,
    sunriseTime: String,
    sunsetTime: String,
    isCelsius: Boolean
) {
    val windDirectionLabel = getWindDirection(currentWindDirection)
    val visibilityKm = "${(hourlyVisibility / 1000)} km"
    val roundedFeelsLike = feelsLike.roundToInt()

    val unitSuffix = if (isCelsius) {
        "°C"
    } else {
        "°F"
    }

    val rows = listOf(
        Pair(
            CurrentWeatherDetailsItem("Feels Like", "${roundedFeelsLike}$unitSuffix"),
            CurrentWeatherDetailsItem("Wind Speed", "$currentWindSpeed km/h $windDirectionLabel")
        ),
        Pair(
            CurrentWeatherDetailsItem("Humidity", "$currentHumidity%"),
            CurrentWeatherDetailsItem("Visibility", visibilityKm)
        ),
        Pair(
            CurrentWeatherDetailsItem("UV Radiation", "$hourlyUvIndex"),
            CurrentWeatherDetailsItem("Precipitation", "$currentPrecipitation mm  |  $hourlyPrecipitationChance% 🌧️")
        ),
        Pair(
            CurrentWeatherDetailsItem("Sunrise", formatSunTime(sunriseTime)),
            CurrentWeatherDetailsItem("Sunset", formatSunTime(sunsetTime))
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.25f))
            .padding(top = 16.dp, bottom = 12.dp) // Uniform padding baseline
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
                CurrentDetailsItemCell(item = pair.first, modifier = Modifier.padding(start = 16.dp).weight(1f))
                CurrentDetailsItemCell(item = pair.second, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun CurrentDetailsItemCell(item: CurrentWeatherDetailsItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.85f)
        )
        Text(
            text = item.value,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

