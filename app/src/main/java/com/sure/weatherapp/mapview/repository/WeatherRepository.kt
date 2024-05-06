package com.sure.weatherapp.mapview.repository

import com.sure.weatherapp.mapview.service.WeatherService
import com.sure.weatherapp.mapview.service.models.LocationKeyResponse
import com.sure.weatherapp.mapview.service.models.SearchLocationResponse
import com.sure.weatherapp.mapview.service.models.WeatherForecastResponse
import com.sure.weatherapp.servicelayer.models.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface WeatherRepository {

    suspend fun getLocationKey(latitudeAndLongitude: String): ServiceResult<LocationKeyResponse>

    suspend fun getForecast(
        key: String,
        isMetricUnit: Boolean
    ): ServiceResult<WeatherForecastResponse>

    suspend fun searchLocation(query: String): ServiceResult<SearchLocationResponse>
}

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherService
) : WeatherRepository {

    override suspend fun getLocationKey(latitudeAndLongitude: String) =
        withContext(Dispatchers.IO) {
        val param = "&q=$latitudeAndLongitude"
        service.getLocationKey(param)
    }

    override suspend fun getForecast(key: String, isMetricUnit: Boolean) =
        withContext(Dispatchers.IO) {
            val param = "&metric=$isMetricUnit"
            service.getForecast(key, param)
        }

    override suspend fun searchLocation(query: String) = withContext(Dispatchers.IO) {
        val param = "&q=$query"
        service.searchLocation(param)
    }


}