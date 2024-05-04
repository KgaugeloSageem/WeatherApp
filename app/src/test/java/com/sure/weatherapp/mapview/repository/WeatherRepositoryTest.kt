package com.sure.weatherapp.mapview.repository

import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.whenever
import com.sure.weatherapp.mapview.service.WeatherService
import com.sure.weatherapp.mapview.service.models.DailyForecast
import com.sure.weatherapp.mapview.service.models.Headline
import com.sure.weatherapp.mapview.service.models.LocationKeyResponse
import com.sure.weatherapp.mapview.service.models.Temperature
import com.sure.weatherapp.mapview.service.models.TemperatureValue
import com.sure.weatherapp.mapview.service.models.Weather
import com.sure.weatherapp.mapview.service.models.WeatherForecastResponse
import com.sure.weatherapp.servicelayer.models.ResponseType
import com.sure.weatherapp.servicelayer.models.ServiceResponse
import com.sure.weatherapp.servicelayer.models.ServiceResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class WeatherRepositoryTest {

    private val service = mock(WeatherService::class.java)
    private val weatherRepo = WeatherRepositoryImpl(service)

    @Test
    fun getForecast_Successful() = runBlocking {
        // Arrange
        val locationKeyResponse = ServiceResult<LocationKeyResponse>(
            serviceResponse = ServiceResponse(ResponseType.SUCCESS),
            data = LocationKeyResponse("13456")
        )
        val forecastResponse = ServiceResult<WeatherForecastResponse>(
            serviceResponse = ServiceResponse(ResponseType.SUCCESS),
            data = WeatherForecastResponse(Headline("", "Sage"), listOf(DailyForecast("", Temperature(
                TemperatureValue(0.5, ""),TemperatureValue(0.5, "")
            ), Weather(""), Weather("")
            )))
        )
        val latitudeAndLongitude = "-123,456"
        whenever(service.getLocationKey("&q=-123,456")).thenReturn(locationKeyResponse)
        whenever(service.getForecast("13456", "&metric=true")).thenReturn(forecastResponse)

        // Act
        val result = weatherRepo.getForecast(latitudeAndLongitude, true)

        // Assert
        Mockito.verify(service).getLocationKey("&q=-123,456")
        Mockito.verify(service).getForecast("13456", "&metric=true")
        assertEquals(result.data?.headline?.text, "Sage")
    }

    @Test
    fun getForecast_UnSuccessful() = runBlocking {
        // Arrange
        val locationKeyResponse = ServiceResult<LocationKeyResponse>(
            serviceResponse = ServiceResponse(ResponseType.SUCCESS),
            data = LocationKeyResponse("13456")
        )
        val forecastResponse = ServiceResult<WeatherForecastResponse>(
            serviceResponse = ServiceResponse(ResponseType.ERROR)
        )
        val latitudeAndLongitude = "-123,456"
        whenever(service.getLocationKey("&q=-123,456")).thenReturn(locationKeyResponse)
        whenever(service.getForecast("13456", "&metric=true")).thenReturn(forecastResponse)

        // Act
        val result = weatherRepo.getForecast(latitudeAndLongitude, true)

        // Assert
        Mockito.verify(service).getLocationKey("&q=-123,456")
        Mockito.verify(service).getForecast("13456", "&metric=true")
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
    }

    @Test
    fun getForecast_GetLocationKey_UnsuccessfulSuccessful() = runBlocking {
        // Arrange
        val locationKeyResponse = ServiceResult<LocationKeyResponse>(
            serviceResponse = ServiceResponse(ResponseType.ERROR)
        )

        val latitudeAndLongitude = "-123,456"
        whenever(service.getLocationKey("&q=-123,456")).thenReturn(locationKeyResponse)

        // Act
        val result = weatherRepo.getForecast(latitudeAndLongitude, true)

        // Assert
        Mockito.verify(service).getLocationKey("&q=-123,456")
        Mockito.verify(service, times(0)).getForecast("", "")
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
    }
}