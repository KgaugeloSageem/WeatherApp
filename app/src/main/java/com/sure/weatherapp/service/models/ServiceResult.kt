package com.sure.weatherapp.service.models

class ServiceResult<T>(
    var serviceResponse: ServiceResponse = ServiceResponse(),
    var data: T? = null
)