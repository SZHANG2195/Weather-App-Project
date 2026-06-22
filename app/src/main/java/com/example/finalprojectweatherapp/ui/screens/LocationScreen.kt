package com.example.finalprojectweatherapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.finalprojectweatherapp.ui.WeatherViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalprojectweatherapp.data.CurrentCityWeatherData
import com.example.finalprojectweatherapp.data.GeocodeResponseData
import com.example.finalprojectweatherapp.utilities.PresetLocation
import com.example.finalprojectweatherapp.utilities.convertToFahrenheit
import com.example.finalprojectweatherapp.utilities.getWeatherEmoji
import com.example.finalprojectweatherapp.utilities.presetLocations
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationScreen(
    viewModel : WeatherViewModel,
    onLocationConfirmed : (Double, Double, String, String) -> Unit,
    onBackClick : () -> Unit
) {
    val cityData by viewModel.currentCityTemperatures.collectAsState()
    val searchResults by viewModel.geocodeSearchResults.collectAsState()
    val isCelsius by viewModel.isCelsius.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.updateSearchQuery("")
        }
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            Column {
                LocationTopBar(
                    searchQuery = viewModel.locationUIState.searchQuery,
                    onSearchQueryChange = {
                        viewModel.updateSearchQuery(it)
                    },
                    onBackClick = onBackClick,
                    onManualInputClick = {
                        viewModel.toggleUserManualInput(true)
                    }
                )
                Spacer(modifier = Modifier.padding(vertical = 4.dp))
                HorizontalDivider(
                    color = Color.DarkGray,
                    thickness = 1.dp
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            if (viewModel.locationUIState.showManualInput) {
                ManualInputDialog(
                    latitudeInput = viewModel.locationUIState.latitudeInput,
                    longitudeInput = viewModel.locationUIState.longitudeInput,
                    onLatChange = {
                        viewModel.updateCoordinateInputs(it, viewModel.locationUIState.longitudeInput)
                    },
                    onLonChange = {
                        viewModel.updateCoordinateInputs(viewModel.locationUIState.latitudeInput, it)
                    },
                    onConfirm = {
                        val latitude = viewModel.locationUIState.latitudeInput.toDoubleOrNull()
                        val longitude = viewModel.locationUIState.longitudeInput.toDoubleOrNull()
                        if (latitude != null && longitude != null) {
                            onLocationConfirmed(latitude, longitude, "$latitude, $longitude", "Custom Location")
                            viewModel.toggleUserManualInput(false)
                        }
                    },
                    onDismiss = {
                        viewModel.toggleUserManualInput(false)
                    }
                )
            }

            if (viewModel.locationUIState.searchQuery.isEmpty()) {
                PresetCityList(
                    presetLocations = presetLocations,
                    cityData = cityData,
                    isCelsius = isCelsius,
                    onLocationConfirmed = onLocationConfirmed
                )
            } else {
                SearchResultList(
                    searchResults = searchResults,
                    onLocationConfirmed = onLocationConfirmed
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationTopBar(
    searchQuery : String,
    onSearchQueryChange : (String) -> Unit,
    onBackClick : () -> Unit,
    onManualInputClick : () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF1A1A1A))
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = { Text("Search cities...", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onManualInputClick) {
                Icon(Icons.Filled.Edit, contentDescription = "Manual Input", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
    )
}

@Composable
private fun ManualInputDialog(
    latitudeInput : String,
    longitudeInput : String,
    onLatChange : (String) -> Unit,
    onLonChange : (String) -> Unit,
    onConfirm : () -> Unit,
    onDismiss : () -> Unit
) {
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text("Enter Coordinates", color = Color.White)
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = latitudeInput,
                    onValueChange = {
                        onLatChange(it)
                        showError = false
                    },
                    label = { Text("Latitude", color = Color.Gray) },
                    singleLine = true,
                    isError = showError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color.Red, // add this too
                        errorTextColor = Color.White
                    )
                )
                OutlinedTextField(
                    value = longitudeInput,
                    onValueChange = {
                        onLonChange(it)
                        showError = false
                    },
                    label = { Text("Longitude", color = Color.Gray) },
                    singleLine = true,
                    isError = showError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.Gray,
                        errorBorderColor = Color.Red, // add this too
                        errorTextColor = Color.White

                    )
                )
                if (showError) {
                    Text(
                        "Please input valid coordinate values",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val latitude = latitudeInput.toDoubleOrNull()
                    val longitude = longitudeInput.toDoubleOrNull()
                    if (latitude != null && longitude != null) {
                        onConfirm()
                    } else {
                        showError = true
                    }
                }
            ) {
                Text("Confirm", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.Gray)
            }
        }
    )
}

@Composable
private fun PresetCityList(
    presetLocations : List<PresetLocation>,
    cityData : Map<String, CurrentCityWeatherData>,
    isCelsius: Boolean,
    onLocationConfirmed : (Double, Double, String, String) -> Unit
) {
    LazyColumn {
        items(presetLocations) { preset ->
            PresetCityItem(
                preset = preset,
                weatherData = cityData[preset.locationName],
                isCelsius = isCelsius,
                onClick = {
                    val subtitle = listOfNotNull(preset.admin1, preset.country).joinToString(", ")
                    onLocationConfirmed(preset.latitude, preset.longitude, preset.locationName, subtitle)
                }
            )
        }
    }
}

@Composable
private fun PresetCityItem(
    preset : PresetLocation,
    weatherData : CurrentCityWeatherData?,
    isCelsius: Boolean,
    onClick : () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(preset.locationName, color = Color.White)
        },
        supportingContent = {
            val subtitle = listOfNotNull(preset.admin1, preset.country).joinToString(", ")
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        trailingContent = {
            if (weatherData != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (weatherData.currentWeatherCode != 0 && weatherData.currentWeatherCode != 1) {
                        Text(
                            getWeatherEmoji(weatherData.currentWeatherCode),
                            fontSize = 18.sp
                        )
                    }
                    Text(
                        if (weatherData.dayNight == 1) {
                            "☀️"
                        } else {
                            "🌙"
                        },
                        fontSize = 18.sp
                    )

                    val displayTemp = if(isCelsius) {
                        weatherData.currentTemp
                    } else {
                        convertToFahrenheit(weatherData.currentTemp)
                    }
                    val unitSuffix = if(isCelsius) {
                        "°C"
                    } else {
                        "°F"
                    }
                    val roundedTemp = displayTemp.roundToInt()

                    Text(
                        "${roundedTemp}${unitSuffix}",
                        color = Color.White
                    )
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Black),
        modifier = Modifier.clickable {
            onClick()
        }
    )
    HorizontalDivider(color = Color(0xFF1A1A1A), thickness = 1.dp)
}

@Composable
private fun SearchResultList(
    searchResults : List<GeocodeResponseData>,
    onLocationConfirmed : (Double, Double, String, String) -> Unit
) {
    LazyColumn {
        items(searchResults) { result ->
            SearchResultItem(
                result = result,
                onClick = {
                    val subtitle = listOfNotNull(result.cityState, result.cityCountry).joinToString(", ")
                    onLocationConfirmed(result.cityLatitude, result.cityLongitude, result.cityName, subtitle)
                }
            )
        }
    }
}

@Composable
private fun SearchResultItem(
    result : GeocodeResponseData,
    onClick : () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(result.cityName, color = Color.White)
        },
        supportingContent = {
            val subtitle = listOfNotNull(result.cityState, result.cityCountry).joinToString(", ")
            if (subtitle.isNotEmpty()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Black),
        modifier = Modifier.clickable {
            onClick()
        }
    )
    HorizontalDivider(color = Color(0xFF1A1A1A), thickness = 1.dp)
}


