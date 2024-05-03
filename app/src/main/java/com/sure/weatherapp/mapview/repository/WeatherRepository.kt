package com.sure.weatherapp.mapview.repository

import com.sure.weatherapp.mapview.service.WeatherService
import com.sure.weatherapp.mapview.service.models.GetLocationKeyRequest
import com.sure.weatherapp.mapview.service.models.GetLocationKeyResponse
import com.sure.weatherapp.servicelayer.models.ServiceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface WeatherRepository {

    suspend fun getLocationKey(latitudeAndLongitude: String): ServiceResult<GetLocationKeyResponse>
}

class WeatherRepositoryImpl @Inject constructor(
    private val service: WeatherService
): WeatherRepository {

    override suspend fun getLocationKey(latitudeAndLongitude: String) = withContext(Dispatchers.IO) {
        val param = "&q=$latitudeAndLongitude"
        service.getLocationKey(param)
    }
}