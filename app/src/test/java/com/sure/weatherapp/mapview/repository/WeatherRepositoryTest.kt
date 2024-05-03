package com.sure.weatherapp.mapview.repository

import com.nhaarman.mockitokotlin2.whenever
import com.sure.weatherapp.mapview.service.WeatherService
import com.sure.weatherapp.mapview.service.models.GetLocationKeyResponse
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
    fun getLocationKey_Successful() = runBlocking {
        // Arrange
        val response = ServiceResult<GetLocationKeyResponse>(
            serviceResponse = ServiceResponse(ResponseType.SUCCESS),
            data = GetLocationKeyResponse("13456")
        )
        val latitudeAndLongitude = "-123,456"
        whenever(service.getLocationKey("&q=-123,456")).thenReturn(response)

        // Act
        val result = weatherRepo.getLocationKey(latitudeAndLongitude)

        // Assert
        Mockito.verify(service).getLocationKey("&q=-123,456")
        assertEquals(result.data?.Key, "13456")
    }

    @Test
    fun getLocationKey_UnSuccessful() = runBlocking {
        // Arrange
        val response = ServiceResult<GetLocationKeyResponse>(
            serviceResponse = ServiceResponse(ResponseType.ERROR),
        )
        val latitudeAndLongitude = "-123,456"
        whenever(service.getLocationKey("&q=-123,456")).thenReturn(response)

        // Act
        val result = weatherRepo.getLocationKey(latitudeAndLongitude)

        // Assert
        Mockito.verify(service).getLocationKey("&q=-123,456")
        assertEquals(result.data?.Key, null)
        assertEquals(result.serviceResponse.responseType, ResponseType.ERROR)
    }

}