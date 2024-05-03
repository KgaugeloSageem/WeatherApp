package com.sure.weatherapp.mapview

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sure.weatherapp.mapview.repository.WeatherRepository
import com.sure.weatherapp.servicelayer.models.ResponseType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    application: Application,
    private val repository: WeatherRepository
): AndroidViewModel(application) {

    private val _locationKey = MutableStateFlow("")
    val locationKey: StateFlow<String> = _locationKey.asStateFlow()

    fun getKey() {
        viewModelScope.launch {
            val longitudeLatitude = "-26.024826%2C%2027.969445"
            val response = repository.getLocationKey(longitudeLatitude)

            if (response.serviceResponse.responseType == ResponseType.SUCCESS){
                _locationKey.value = response.data?.Key.toString()
                println("Sage: Success")
            }else {
                println("Sage: Error - ${response.serviceResponse.message}")
            }
        }
    }

}