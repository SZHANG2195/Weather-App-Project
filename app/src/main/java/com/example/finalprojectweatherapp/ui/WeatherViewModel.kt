package com.example.finalprojectweatherapp.ui

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.finalprojectweatherapp.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

sealed class WeatherUIState {
    object Loading : WeatherUIState()

    data class Success(val data : WeatherData) : WeatherUIState()
    data class Error(val message : String) : WeatherUIState()
}

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()
    private var searchTask : Job? = null
    private val _uiState = MutableStateFlow<WeatherUIState>(WeatherUIState.Loading)
    val uiState : StateFlow<WeatherUIState> = _uiState.asStateFlow()
    private val _currentCityTemperatures = MutableStateFlow<Map<String, CurrentCityWeatherData>>(emptyMap())
    val currentCityTemperatures : StateFlow<Map<String, CurrentCityWeatherData>> = _currentCityTemperatures.asStateFlow()
    private val _geocodeSearchResults = MutableStateFlow<List<GeocodeResponseData>>(emptyList())
    val geocodeSearchResults : StateFlow<List<GeocodeResponseData>> = _geocodeSearchResults.asStateFlow()
    private val _isCelsius = MutableStateFlow(false)
    val isCelsius = _isCelsius.asStateFlow()

    val lastUpdated by derivedStateOf {
        val updating = isRefreshing
        val currentState = _uiState.value
        if (updating) {
            "Updating"
        } else {
            when (currentState) {
                is WeatherUIState.Success -> {
                    val time = LocalDateTime.now()
                    "Last Updated: ${time.format(DateTimeFormatter.ofPattern("h:mm a"))}"
                }
                else -> " Last Updated: N/A"
            }
        }
    }

    var locationUIState by mutableStateOf(LocationUIState())
        private set
    var latitude by mutableDoubleStateOf(40.7143)
        private set
    var longitude by mutableDoubleStateOf(-74.006)
        private set
    var selectedLocationName by mutableStateOf("New York")
        private set
    var selectedLocationSubtitle by mutableStateOf("New York, United States")
        private set

    var isRefreshing by mutableStateOf(false)
        private set

    init {
        fetchWeather()
        fetchCurrentCityTemperatures()
    }

    fun fetchWeather() {
        if (isRefreshing) {
            return
        }

        _uiState.value = WeatherUIState.Loading
        isRefreshing = true

        viewModelScope.launch {
            val result = repository.fetchWeatherData(latitude, longitude)
            result.onSuccess { data ->
                _uiState.value = WeatherUIState.Success(data)
                isRefreshing = false
            }
            result.onFailure {
                _uiState.value = WeatherUIState.Error("Unable to fetch weather data at this time")
                isRefreshing = false
            }
        }
    }

    fun fetchCurrentCityTemperatures() {
        viewModelScope.launch {
            val result = repository.fetchCurrentCityData()
            result.onSuccess { data ->
                _currentCityTemperatures.value = data
            }
        }
    }

    fun fetchGeocodeSearchResults(searchQuery : String) {
        viewModelScope.launch {
            if (searchQuery.isBlank()) {
                _geocodeSearchResults.value = emptyList()
                return@launch
            }
            val result = repository.fetchGeocodeData(searchQuery)
            result.onSuccess { data ->
                _geocodeSearchResults.value = data
            }
        }
    }

    fun changeLocation(newLatitude : Double, newLongitude : Double, cityName : String, subtitle : String) {
        latitude = newLatitude
        longitude = newLongitude
        selectedLocationName = cityName
        selectedLocationSubtitle = subtitle
        _geocodeSearchResults.value = emptyList()
        fetchWeather()
    }

    fun toggleUserManualInput(toggleManualInput : Boolean) {
        locationUIState = locationUIState.copy(showManualInput = toggleManualInput)
    }

    fun toggleTemperatureUnit() {
        _isCelsius.value = !_isCelsius.value
    }

    fun updateSearchQuery(searchQuery : String) {
        locationUIState = locationUIState.copy(searchQuery = searchQuery)

        searchTask?.cancel()

        if(searchQuery.isBlank()) {
            _geocodeSearchResults.value = emptyList()
            return
        }

        searchTask = viewModelScope.launch {
            delay(400)
            fetchGeocodeSearchResults(searchQuery)
        }
    }

    fun updateCoordinateInputs(latitude : String, longitude : String) {
        locationUIState = locationUIState.copy(latitudeInput = latitude, longitudeInput = longitude)
    }
}