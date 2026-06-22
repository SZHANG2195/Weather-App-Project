package com.example.finalprojectweatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.finalprojectweatherapp.ui.WeatherViewModel
import com.example.finalprojectweatherapp.ui.screens.DetailScreen
import com.example.finalprojectweatherapp.ui.screens.HomeScreen
import com.example.finalprojectweatherapp.ui.screens.LocationScreen
import com.example.finalprojectweatherapp.ui.screens.WeatherStateWrapper

object Routes {
    const val HOME = "homeScreen"
    const val DETAIL = "detailScreen/{dayIndex}"

    const val LOCATION = "locationSelectScreen"

    fun detail(dayIndex : Int) = "detailScreen/$dayIndex"
}

@Composable
fun NavGraph(navController : NavHostController, viewModel: WeatherViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val isCelsius by viewModel.isCelsius.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onToggleUnit = {
                    viewModel.toggleTemperatureUnit()
                },
                onRefresh = {
                    viewModel.fetchWeather()
                    viewModel.fetchCurrentCityTemperatures()
                },
                onLocationClick = {
                    navController.navigate(Routes.LOCATION)
                },
                onDayClick = { index: Int ->
                    navController.navigate(Routes.detail(index))
                }
            )

        }

        composable(Routes.DETAIL) { backStackEntry ->
            val dayIndex = backStackEntry.arguments?.getString("dayIndex")?.toInt() ?: 0
            WeatherStateWrapper(
                uiState = uiState,
                onRefresh = { viewModel.fetchWeather() }
            ) { successState ->
                DetailScreen(
                    dayIndex = dayIndex,
                    weatherData = successState.data,
                    locationTitle = viewModel.selectedLocationName,
                    isCelsius = isCelsius,
                    isRefreshing = viewModel.isRefreshing,
                    lastUpdatedTime = viewModel.lastUpdated,
                    onToggleUnit = {
                        viewModel.toggleTemperatureUnit()
                    },
                    onRefresh = {
                        viewModel.fetchWeather()
                        viewModel.fetchCurrentCityTemperatures()
                    },
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onLocationClick = {
                        navController.navigate(Routes.LOCATION)
                    }
                )
            }
        }

        composable(Routes.LOCATION) {
            LocationScreen(
                viewModel = viewModel,
                onLocationConfirmed = { latitude : Double, longitude : Double, cityName : String, subtitle : String ->
                    viewModel.changeLocation(latitude, longitude, cityName, subtitle)
                    navController.popBackStack()
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}