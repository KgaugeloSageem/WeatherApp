package com.sure.weatherapp.mapview.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Switch
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.sure.weatherapp.MainActivity
import com.sure.weatherapp.mapview.viewmodel.WeatherViewModel
import com.sure.weatherapp.ui.theme.WeatherAppTheme
import kotlinx.coroutines.launch

private val DEFAULT_LAT_LNG = LatLng(-26.013603, 28.004382)


@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: WeatherViewModel,
    navController: NavController
) {

    val currentDayForecast by viewModel.currentDayWeatherDetails.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var switchCheckedState by remember { mutableStateOf(true) }
    var searchQueryInput by remember { mutableStateOf("") }


    val sheetState = rememberModalBottomSheetState()

    val scaffoldSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = sheetState
    )

    val scope = rememberCoroutineScope()


    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(DEFAULT_LAT_LNG, 15f)
    }

    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    var clickedLatLng by remember { mutableStateOf(DEFAULT_LAT_LNG) }

    BottomSheetScaffold(
        scaffoldState = scaffoldSheetState,
        sheetContent = {
            SheetDetailsScreen(
                header = currentDayForecast?.header.toString(),
                locationName = currentDayForecast?.locationName.toString(),
                date = viewModel.getCurrentDateFormatted(),
                minTemp = currentDayForecast?.minTemp.toString(),
                maxTemp = currentDayForecast?.maxTemp.toString(),
                dayDescription = currentDayForecast?.dayDescription.toString(),
                nightDescription = currentDayForecast?.nightDescription.toString(),
                isLoading = isLoading,
                onFiveDayForecastButtonClick = {
                    navController.navigate(MainActivity.FIVE_DAY_FORECAST_SCREEN)
                },
                switchCheckedState = switchCheckedState,
                onCheckedChange = {
                    switchCheckedState = it
                    viewModel.getWeatherForecast(
                        "${clickedLatLng.latitude},${clickedLatLng.longitude}",
                        it
                    )
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(26.dp),
                    query = searchQueryInput,
                    onQueryChange = {
                        searchQueryInput = it
                        viewModel.searchLocation(it)
                    },
                    onSearch = {},
                    active = searchQueryInput.isNotEmpty(),
                    onActiveChange = {},
                    placeholder = { Text(text = "Search Location") }
                ) {
                    LazyColumn(
                        Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth()
                    ) {
                        searchResults?.let { searchResult ->
                            items(items = searchResult.toList(), itemContent = { results ->
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.getForeCastWithKey(
                                                results.key,
                                                switchCheckedState,
                                                results.locationName
                                            )
                                            clickedLatLng = results.latLng
                                            cameraPositionState.move(
                                                CameraUpdateFactory.newCameraPosition(
                                                    CameraPosition.fromLatLngZoom(
                                                        results.latLng,
                                                        15f
                                                    )
                                                )
                                            )
                                            searchQueryInput = ""
                                        }
                                        .fillMaxWidth()
                                        .padding(top = 16.dp, start = 16.dp),
                                    text = results.locationName
                                )
                            })
                        }
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = properties,
                    onMapClick = { latLng ->
                        clickedLatLng = latLng
                        viewModel.getWeatherForecast(
                            "${latLng.latitude},${latLng.longitude}",
                            switchCheckedState
                        )
                        scope.launch {
                            sheetState.expand()
                        }
                    }
                ) {
                    Marker(
                        state = MarkerState(position = clickedLatLng),
                    )

                }
            }
        }
    }



    BackHandler {
        if (sheetState.hasExpandedState) {
            scope.launch {
                sheetState.hide()
            }
        }
    }

    if (sheetState.hasExpandedState && searchQueryInput.isNotEmpty()) {
        scope.launch {
            sheetState.hide()
        }
    } else {
        scope.launch {
            sheetState.expand()
        }
    }

    LaunchedEffect(viewModel) {
        launch {
            sheetState.expand()
        }
        launch {
            viewModel.getWeatherForecast(
                "${clickedLatLng.latitude},${clickedLatLng.longitude}",
                switchCheckedState
            )
        }
    }

}

@Composable
private fun SheetDetailsScreen(
    header: String,
    locationName: String,
    date: String,
    minTemp: String,
    maxTemp: String,
    dayDescription: String,
    nightDescription: String,
    isLoading: Boolean,
    onFiveDayForecastButtonClick: () -> Unit,
    switchCheckedState: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val scrollableState = rememberScrollState()

    if (isLoading) {
        Column(
            modifier = Modifier
                .height(380.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.testTag("progressIndicator")
            )
        }

    } else {
        Column(
            modifier = Modifier
                .height(380.dp)
                .fillMaxWidth()
                .verticalScroll(scrollableState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = header,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            Text(
                modifier = Modifier
                    .padding(top = 16.dp, start = 16.dp)
                    .align(Alignment.Start),
                text = locationName,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            Row(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier,
                    text = "F",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Switch(
                    modifier = Modifier
                        .padding(),
                    checked = switchCheckedState,
                    onCheckedChange = onCheckedChange
                )
                Text(
                    modifier = Modifier,
                    text = "C",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            DailyForecastItem(
                date = date,
                minTemp = minTemp,
                maxTemp = maxTemp,
                dayDescription = dayDescription,
                nightDescription = nightDescription
            )

            Button(
                modifier = Modifier.padding(bottom = 16.dp),
                onClick = onFiveDayForecastButtonClick
            ) {
                Text(text = "SEE 5 DAY FORECAST")
            }

        }
    }
}

@Composable
@Preview
private fun SheetDetailsScreenPreview() {
    WeatherAppTheme {
        SheetDetailsScreen(
            "Pleasant Saturday",
            "Fourways, City of Johannesburg, Gauteng, South Africa",
            "23 May 2015",
            "23 C",
            "34 C",
            "Sunny",
            "Clear",
            false,
            {},
            switchCheckedState = false,
            onCheckedChange = {}
        )
    }
}