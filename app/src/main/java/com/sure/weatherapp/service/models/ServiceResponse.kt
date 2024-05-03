package com.sure.weatherapp.service.models

interface IServiceResponse{
    var responseType: ResponseType
    var code: String?
}

data class ServiceResponse(override var responseType: ResponseType = ResponseType.NONE, override var code: String? = null, var message: String = ""):
    IServiceResponse

enum class ResponseType{
    SUCCESS,
    ERROR,
    CONNECTION_ERROR,
    NONE
}