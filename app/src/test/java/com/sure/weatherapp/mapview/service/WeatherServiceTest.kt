package com.sure.weatherapp.mapview.service

import com.nhaarman.mockitokotlin2.whenever
import com.sure.weatherapp.mapview.service.models.AdministrativeArea
import com.sure.weatherapp.mapview.service.models.Country
import com.sure.weatherapp.mapview.service.models.DailyForecast
import com.sure.weatherapp.mapview.service.models.GeoPosition
import com.sure.weatherapp.mapview.service.models.Headline
import com.sure.weatherapp.mapview.service.models.LocationKeyResponse
import com.sure.weatherapp.mapview.service.models.ParentCity
import com.sure.weatherapp.mapview.service.models.SearchLocationResponse
import com.sure.weatherapp.mapview.service.models.Temperature
import com.sure.weatherapp.mapview.service.models.TemperatureValue
import com.sure.weatherapp.mapview.service.models.Weather
import com.sure.weatherapp.mapview.service.models.WeatherForecastResponse
import com.sure.weatherapp.servicelayer.Service
import com.sure.weatherapp.servicelayer.models.ResponseType
import com.sure.weatherapp.servicelayer.models.ServiceResponse
import com.sure.weatherapp.servicelayer.models.ServiceResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock

class WeatherServiceTest {

    private val mockService = mock(Service::class.java)
    private val deviceService = WeatherServiceImpl(mockService)

    @Test
    fun getLocationKey_Successful() = runBlocking {
        // Arrange
        val response = ServiceResult<LocationKeyResponse>(
            ServiceResponse(ResponseType.SUCCESS),
            LocationKeyResponse("123")
        )
        val params = "&q=-123,456"
        whenever(mockService.GET("locations/v1/cities/geoposition/search", params, LocationKeyResponse::class.java)).thenReturn(response)

        // Act
        val result = deviceService.getLocationKey(params)

        // Assert
        Mockito.verify(mockService).GET("locations/v1/cities/geoposition/search", params ,LocationKeyResponse::class.java)
        assertEquals(result.data?.Key, "123")
    }

    @Test
    fun getForecast_Successful() = runBlocking {
        // Arrange
        val response = ServiceResult<WeatherForecastResponse>(
            serviceResponse = ServiceResponse(ResponseType.SUCCESS),
            data = WeatherForecastResponse(
                Headline("", "Sage"), listOf(
                    DailyForecast("", Temperature(
                TemperatureValue(0.5, ""), TemperatureValue(0.5, "")
            ), Weather(""), Weather("")
            )
                ))
        )
        val isMetricUnit = "true"
        whenever(mockService.GET("forecasts/v1/daily/5day/12345", isMetricUnit, WeatherForecastResponse::class.java)).thenReturn(response)

        // Act
        val result = deviceService.getForecast("12345", isMetricUnit)

        // Assert
        Mockito.verify(mockService).GET("forecasts/v1/daily/5day/12345", isMetricUnit ,WeatherForecastResponse::class.java)
        assertEquals(result.data?.headline?.text, "Sage")
    }

    @Test
    fun searchLocation_Successful() = runBlocking {
        // Arrange
        val response = ServiceResult<SearchLocationResponse>(
            serviceResponse = ServiceResponse(ResponseType.SUCCESS),
            data = SearchLocationResponse(
                "12345",
                "",
                Country(""),
                AdministrativeArea(""),
                GeoPosition(0.3, 0.3),
                ParentCity(""),
                listOf()
            )
        )
        val query = "Boksburg"
        whenever(mockService.GET("locations/v1/search", query, SearchLocationResponse::class.java)).thenReturn(response)

        // Act
        val result = deviceService.searchLocation(query)

        // Assert
        Mockito.verify(mockService).GET("locations/v1/search", query, SearchLocationResponse::class.java)
        assertEquals(result.data?.key, "12345")
    }

    @Test
    fun getLocationKey_UnSuccessful() = runBlocking {
        // Arrange
        val response = ServiceResult<LocationKeyResponse>(
            ServiceResponse(ResponseType.ERROR)
        )
        val params = "&q=-123,456"
        whenever(mockService.GET("locations/v1/cities/geoposition/search", params, LocationKeyResponse::class.java)).thenReturn(response)

        // Act
        val result = deviceService.getLocationKey(params)

        // Assert
        Mockito.verify(mockService).GET("locations/v1/cities/geoposition/search", params ,LocationKeyResponse::class.java)
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
    }

    @Test
    fun getForecast_UnSuccessful() = runBlocking {
        // Arrange
        val response = ServiceResult<WeatherForecastResponse>(
            serviceResponse = ServiceResponse(ResponseType.ERROR)
        )
        val isMetricUnit = "true"
        whenever(mockService.GET("forecasts/v1/daily/5day/12345", isMetricUnit, WeatherForecastResponse::class.java)).thenReturn(response)

        // Act
        val result = deviceService.getForecast("12345", isMetricUnit)

        // Assert
        Mockito.verify(mockService).GET("forecasts/v1/daily/5day/12345", isMetricUnit ,WeatherForecastResponse::class.java)
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
    }

    @Test
    fun searchLocation_UnSuccessful() = runBlocking {
        // Arrange
        val response = ServiceResult<SearchLocationResponse>(
            serviceResponse = ServiceResponse(ResponseType.ERROR)
        )
        val query = "Boksburg"
        whenever(mockService.GET("locations/v1/search", query, SearchLocationResponse::class.java)).thenReturn(response)

        // Act
        val result = deviceService.searchLocation(query)

        // Assert
        Mockito.verify(mockService).GET("locations/v1/search", query, SearchLocationResponse::class.java)
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
    }

}