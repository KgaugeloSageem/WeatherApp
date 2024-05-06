package com.sure.weatherapp.mapview.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sure.weatherapp.mapview.viewmodel.WeatherViewModel

@Composable
fun FiveDayForecastScreen(
    viewModel: WeatherViewModel
) {

    val fiveDailyForecast by viewModel.fiveDayForecasts.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyColumn(
                Modifier
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                fiveDailyForecast?.let { dailyForecasts ->
                    items(items = dailyForecasts.toList(), itemContent = {
                        DailyForecastItem(
                            modifier = Modifier
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                            date = viewModel.getFormattedDate(it.date).toString(),
                            minTemp = it.minTemp,
                            maxTemp = it.maxTemp,
                            dayDescription = it.dayDescription,
                            nightDescription = it.nightDescription
                        )
                    })
                }
            }

        }
    }
}