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
    private val _isLoading = MutableStateFlow(false)

    val currentDayWeatherDetails: StateFlow<WeatherForecastDetails?> =
        _currentDayWeatherDetails.asStateFlow()
    val fiveDayForecasts: StateFlow<List<WeatherForecastDetails>?> = _fiveDayForecasts.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getWeatherForecast(latitudeAndLongitude: String, isMetricUnit: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            val getKeyResponse = repository.getLocationKey(latitudeAndLongitude)

            if (getKeyResponse.serviceResponse.responseType == ResponseType.SUCCESS) {
                val response =
                    repository.getForecast(getKeyResponse.data?.key.toString(), isMetricUnit)

                if (response.serviceResponse.responseType == ResponseType.SUCCESS) {
                    val localizedName = getKeyResponse.data?.localizedName
                    val supplementalAdminAreas =
                        if (getKeyResponse.data?.supplementalAdminAreas?.isEmpty() == true) "" else "${
                            getKeyResponse.data?.supplementalAdminAreas?.get(0)?.localizedName
                        },"
                    val administrativeArea = getKeyResponse.data?.administrativeArea?.localizedName
                    val country = getKeyResponse.data?.country?.localizedName

                    val location =
                        "$localizedName, $supplementalAdminAreas $administrativeArea, $country"
                    _currentDayWeatherDetails.value =
                        response.data?.toCurrentDayForecastDetails(location)

                    _fiveDayForecasts.value =
                        response.data?.getFiveDayForecastList(latitudeAndLongitude)
                }
            }
            _isLoading.value = false
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
