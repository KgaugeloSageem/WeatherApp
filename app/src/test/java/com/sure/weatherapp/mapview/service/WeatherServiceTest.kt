package com.sure.weatherapp.mapview.service

import com.nhaarman.mockitokotlin2.whenever
import com.sure.weatherapp.mapview.service.models.GetLocationKeyResponse
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
        val response = ServiceResult<GetLocationKeyResponse>(
            ServiceResponse(ResponseType.SUCCESS),
            GetLocationKeyResponse("123")
        )
        val params = "&q=-123,456"
        whenever(mockService.GET("locations/v1/cities/geoposition/search", params, GetLocationKeyResponse::class.java)).thenReturn(response)

        // Act
        val result = deviceService.getLocationKey(params)

        // Assert
        Mockito.verify(mockService).GET("locations/v1/cities/geoposition/search", params ,GetLocationKeyResponse::class.java)
        assertEquals(result.data?.Key, "123")
    }

    @Test
    fun getLocationKey_UnSuccessful() = runBlocking {
        // Arrange
        val response = ServiceResult<GetLocationKeyResponse>(
            ServiceResponse(ResponseType.ERROR)
        )
        val params = "&q=-123,456"
        whenever(mockService.GET("locations/v1/cities/geoposition/search", params, GetLocationKeyResponse::class.java)).thenReturn(response)

        // Act
        val result = deviceService.getLocationKey(params)

        // Assert
        Mockito.verify(mockService).GET("locations/v1/cities/geoposition/search", params ,GetLocationKeyResponse::class.java)
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
    }
}