package com.sure.weatherapp.servicelayer.models

class ServiceResult<T>(
    var serviceResponse: ServiceResponse = ServiceResponse(),
    var data: T? = null
)