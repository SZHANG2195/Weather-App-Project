package com.example.finalprojectweatherapp.ui

data class LocationUIState(
    val isSearchActive : Boolean = false,
    val showManualInput : Boolean = false,
    val searchQuery : String = "",
    val latitudeInput : String = "",
    val longitudeInput : String = ""
)