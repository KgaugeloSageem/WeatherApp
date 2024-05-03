package com.sure.weatherapp.mapview.service

import com.sure.weatherapp.mapview.service.models.GetLocationKeyRequest
import com.sure.weatherapp.mapview.service.models.GetLocationKeyResponse
import com.sure.weatherapp.servicelayer.Service
import com.sure.weatherapp.servicelayer.models.ServiceResult
import javax.inject.Inject

interface WeatherService {

   suspend fun getLocationKey(latitudeAndLongitude: String): ServiceResult<GetLocationKeyResponse>

}

const val GET_LOCATION_KEY_URL = "locations/v1/cities/geoposition/search"

class WeatherServiceImpl @Inject constructor(
    private val service: Service
): WeatherService {

    override suspend fun getLocationKey(latitudeAndLongitude: String): ServiceResult<GetLocationKeyResponse> {
       return service.GET(url = GET_LOCATION_KEY_URL, parameters = latitudeAndLongitude, responseType = GetLocationKeyResponse::class.java)
    }

}