package com.sure.weatherapp.mapview.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sure.weatherapp.mapview.repository.WeatherRepository
import com.sure.weatherapp.mapview.service.models.WeatherForecastDetails
import com.sure.weatherapp.mapview.service.models.WeatherForecastResponse.Companion.getFiveDayForecastList
import com.sure.weatherapp.mapview.service.models.WeatherForecastResponse.Companion.toCurrentDayForecastDetails
import com.sure.weatherapp.servicelayer.models.ResponseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    application: Application,
    private val repository: WeatherRepository
): AndroidViewModel(application) {

    private val _currentDayWeatherDetails = MutableStateFlow<WeatherForecastDetails?>(null)
    private val _fiveDayForecasts = MutableStateFlow<List<WeatherForecastDetails>?>(null)
    private val _searchResults = MutableStateFlow<List<SearchResults>?>(listOf())
    private val _isLoading = MutableStateFlow(false)

    val currentDayWeatherDetails: StateFlow<WeatherForecastDetails?> =
        _currentDayWeatherDetails.asStateFlow()
    val fiveDayForecasts: StateFlow<List<WeatherForecastDetails>?> = _fiveDayForecasts.asStateFlow()
    val searchResults: StateFlow<List<SearchResults>?> = _searchResults.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getWeatherForecast(latitudeAndLongitude: String, isMetricUnit: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            val getKeyResponse = repository.getLocationKey(latitudeAndLongitude)

            if (getKeyResponse.serviceResponse.responseType == ResponseType.SUCCESS) {
                val localizedName = getKeyResponse.data?.localizedName
                val supplementalAdminAreas =
                    if (getKeyResponse.data?.supplementalAdminAreas?.isEmpty() == true) "" else "${
                        getKeyResponse.data?.supplementalAdminAreas?.get(0)?.localizedName
                    },"
                val administrativeArea = getKeyResponse.data?.administrativeArea?.localizedName
                val country = getKeyResponse.data?.country?.localizedName

                val location =
                    "$localizedName, $supplementalAdminAreas $administrativeArea, $country"

                getForeCastWithKey(getKeyResponse.data?.key.toString(), isMetricUnit, location)
            }
            _isLoading.value = false
        }
    }

    fun getForeCastWithKey(key: String, isMetricUnit: Boolean, locationName: String) {
        viewModelScope.launch {
            val response =
                repository.getForecast(key, isMetricUnit)

            if (response.serviceResponse.responseType == ResponseType.SUCCESS) {

                _currentDayWeatherDetails.value =
                    response.data?.toCurrentDayForecastDetails(locationName)

                _fiveDayForecasts.value =
                    response.data?.getFiveDayForecastList(locationName)
            }
        }
    }

    fun searchLocation(query: String) {
        viewModelScope.launch {
            val response = repository.searchLocation(query)
            if (response.serviceResponse.responseType == ResponseType.SUCCESS) {
                val searchResult = response.data?.map {
                    val localizedName = it.localizedName
                    val supplementalAdminAreas =
                        if (it.supplementalAdminAreas.isEmpty()) "" else "${it.supplementalAdminAreas[0].localizedName},"
                    val administrativeArea = it.administrativeArea.localizedName
                    val country = it.country.localizedName

                    val locationName =
                        "$localizedName, $supplementalAdminAreas $administrativeArea, $country"
                    SearchResults(it.key, locationName)
                }
                _searchResults.value = searchResult
            }
        }
    }

    fun getCurrentDateFormatted(): String {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }

    fun getFormattedDate(date: String): String? {
        val dateOnly = date.substring(0, 10)
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

        val inputDate = inputFormat.parse(dateOnly)

        return inputDate?.let { outputFormat.format(it) }
    }
}

data class SearchResults(val key: String, val locationName: String)
