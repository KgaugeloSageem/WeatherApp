package com.sure.weatherapp.mapview.repository

import com.nhaarman.mockitokotlin2.whenever
import com.sure.weatherapp.mapview.service.WeatherService
import com.sure.weatherapp.mapview.service.models.AdministrativeArea
import com.sure.weatherapp.mapview.service.models.Country
import com.sure.weatherapp.mapview.service.models.DailyForecast
import com.sure.weatherapp.mapview.service.models.GeoPosition
import com.sure.weatherapp.mapview.service.models.Headline
import com.sure.weatherapp.mapview.service.models.LocationKeyResponse
import com.sure.weatherapp.mapview.service.models.SearchLocationResponse
import com.sure.weatherapp.mapview.service.models.Temperature
import com.sure.weatherapp.mapview.service.models.TemperatureValue
import com.sure.weatherapp.mapview.service.models.Weather
import com.sure.weatherapp.mapview.service.models.WeatherForecastResponse
import com.sure.weatherapp.servicelayer.models.ResponseType
import com.sure.weatherapp.servicelayer.models.ServiceResponse
import com.sure.weatherapp.servicelayer.models.ServiceResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class WeatherRepositoryTest {

    private val service = mock(WeatherService::class.java)
    private val weatherRepo = WeatherRepositoryImpl(service)

    @Test
    fun getLocationKey_Successful() = runBlocking {
        // Arrange
        val locationKeyResponse = ServiceResult<LocationKeyResponse>(
            serviceResponse = ServiceResponse(ResponseType.SUCCESS),
            data = LocationKeyResponse(
                "13456",
                "",
                Country(""),
                AdministrativeArea(""),
                supplementalAdminAreas = listOf()
            )
        )

        val latitudeAndLongitude = "-123,456"
        whenever(service.getLocationKey("&q=-123,456")).thenReturn(locationKeyResponse)

        // Act
        val result = weatherRepo.getLocationKey(latitudeAndLongitude)

        // Assert
        Mockito.verify(service).getLocationKey("&q=-123,456")
        assertEquals(result.data?.key, "13456")
    }

    @Test
    fun getForecast_Successful() = runBlocking {
        // Arrange
        val forecastResponse = ServiceResult<WeatherForecastResponse>(
            serviceResponse = ServiceResponse(ResponseType.SUCCESS),
            data = WeatherForecastResponse(Headline("", "Sage"), listOf(DailyForecast("", Temperature(
                TemperatureValue(0.5, ""),TemperatureValue(0.5, "")
            ), Weather(""), Weather("")
            )))
        )
        whenever(service.getForecast("13456", "&metric=true")).thenReturn(forecastResponse)

        // Act
        val result = weatherRepo.getForecast("13456", true)

        // Assert
        Mockito.verify(service).getForecast("13456", "&metric=true")
        assertEquals(result.data?.headline?.text, "Sage")
    }

    @Test
    fun searchLocation_Successful() = runBlocking {
        // Arrange
        val forecastResponse = ServiceResult<SearchLocationResponse>(
            serviceResponse = ServiceResponse(ResponseType.SUCCESS),
            data = SearchLocationResponse(
                "12345",
                "",
                Country(""),
                AdministrativeArea(""),
                GeoPosition(0.3, 0.3),
                listOf()
            )
        )
        whenever(service.searchLocation("&q=Boksburg")).thenReturn(forecastResponse)

        // Act
        val result = weatherRepo.searchLocation("Boksburg")

        // Assert
        Mockito.verify(service).searchLocation("&q=Boksburg")
        assertEquals(result.data?.key, "12345")
    }

    @Test
    fun getForecast_UnSuccessful() = runBlocking {
        // Arrange
        val forecastResponse = ServiceResult<WeatherForecastResponse>(
            serviceResponse = ServiceResponse(ResponseType.ERROR)
        )
        whenever(service.getForecast("13456", "&metric=true")).thenReturn(forecastResponse)

        // Act
        val result = weatherRepo.getForecast("13456", true)

        // Assert
        Mockito.verify(service).getForecast("13456", "&metric=true")
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
        assertNull(result.data)
    }

    @Test
    fun getLocationKey_UnsuccessfulSuccessful() = runBlocking {
        // Arrange
        val locationKeyResponse = ServiceResult<LocationKeyResponse>(
            serviceResponse = ServiceResponse(ResponseType.ERROR)
        )

        val latitudeAndLongitude = "-123,456"
        whenever(service.getLocationKey("&q=-123,456")).thenReturn(locationKeyResponse)

        // Act
        val result = weatherRepo.getLocationKey(latitudeAndLongitude)

        // Assert
        Mockito.verify(service).getLocationKey("&q=-123,456")
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
        assertNull(result.data)
    }

    @Test
    fun searchLocation_UnSuccessful() = runBlocking {
        // Arrange
        val forecastResponse = ServiceResult<SearchLocationResponse>(
            serviceResponse = ServiceResponse(ResponseType.ERROR)
        )
        whenever(service.searchLocation("&q=Boksburg")).thenReturn(forecastResponse)

        // Act
        val result = weatherRepo.searchLocation("Boksburg")

        // Assert
        Mockito.verify(service).searchLocation("&q=Boksburg")
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
        assertNull(result.data)
    }
}