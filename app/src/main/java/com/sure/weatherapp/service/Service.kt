package com.sure.weatherapp.service

import com.sure.weatherapp.service.models.ServiceResult

interface Service {

    suspend fun <T : Any> GET(url: String, responseType: Class<T>): ServiceResult<T>

    suspend fun <T : Any> GET(url: String, request: Any, responseType: Class<T>): ServiceResult<T>
}