package com.sure.weatherapp.mapview.service

import com.sure.weatherapp.mapview.service.models.LocationKeyResponse
import com.sure.weatherapp.mapview.service.models.SearchLocationResponse
import com.sure.weatherapp.mapview.service.models.WeatherForecastResponse
import com.sure.weatherapp.servicelayer.Service
import com.sure.weatherapp.servicelayer.models.ServiceResult
import javax.inject.Inject

interface WeatherService {

   suspend fun getLocationKey(latitudeAndLongitude: String): ServiceResult<LocationKeyResponse>

   suspend fun getForecast(locationKey: String ,isMetricUnit: String): ServiceResult<WeatherForecastResponse>

   suspend fun searchLocation(query: String): ServiceResult<SearchLocationResponse>

}

const val GET_LOCATION_KEY_URL = "locations/v1/cities/geoposition/search"
const val GET_FORECAST_URL = "forecasts/v1/daily/5day/%s"
const val SEARCH_LOCATION_URL = "locations/v1/search"

class WeatherServiceImpl @Inject constructor(
    private val service: Service
): WeatherService {

    override suspend fun getLocationKey(latitudeAndLongitude: String): ServiceResult<LocationKeyResponse> {
       return service.GET(url = GET_LOCATION_KEY_URL, parameters = latitudeAndLongitude, responseType = LocationKeyResponse::class.java)
    }

    override suspend fun getForecast(locationKey: String ,isMetricUnit: String): ServiceResult<WeatherForecastResponse> {
        return service.GET(GET_FORECAST_URL.format(locationKey), isMetricUnit, WeatherForecastResponse::class.java)
    }

    override suspend fun searchLocation(query: String): ServiceResult<SearchLocationResponse> {
        return service.GET(SEARCH_LOCATION_URL, query, SearchLocationResponse::class.java)
    }

}