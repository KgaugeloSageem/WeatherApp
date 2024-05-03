package com.sure.weatherapp.servicelayer

import com.sure.weatherapp.servicelayer.models.ServiceResult

interface Service {

    suspend fun <T : Any> GET(url: String, parameters: String, responseType: Class<T>): ServiceResult<T>
}