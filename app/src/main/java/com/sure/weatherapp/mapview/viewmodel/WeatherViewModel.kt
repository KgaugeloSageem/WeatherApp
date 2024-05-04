package com.sure.weatherapp.mapview.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sure.weatherapp.mapview.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    application: Application,
    private val repository: WeatherRepository
): AndroidViewModel(application) {

    //TODO to be implemented
}