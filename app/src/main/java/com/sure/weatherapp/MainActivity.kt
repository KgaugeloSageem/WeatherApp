package com.sure.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sure.weatherapp.mapview.ui.FiveDayForecastScreen
import com.sure.weatherapp.mapview.ui.MapScreen
import com.sure.weatherapp.mapview.viewmodel.WeatherViewModel
import com.sure.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val viewModel: WeatherViewModel = viewModel()

                    NavHost(navController, startDestination = MAP_SCREEN) {

                        composable(MAP_SCREEN) {
                            MapScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }

                        composable(FIVE_DAY_FORECAST_SCREEN) {
                            FiveDayForecastScreen(viewModel)
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val MAP_SCREEN = "map-screen-route"
        const val FIVE_DAY_FORECAST_SCREEN = "five-day-forecast-screen-route"
    }
}
