package com.sure.weatherapp.servicelayer.models

data class ServiceResponse(var responseType: ResponseType = ResponseType.NONE, var message: String = "")

enum class ResponseType{
    SUCCESS,
    ERROR,
    CONNECTION_ERROR,
    NONE
}