package com.sure.weatherapp.mapview.service.models

import com.google.gson.annotations.SerializedName

data class WeatherForecastResponse(
    @SerializedName("Headline") val headline: Headline,
    @SerializedName("DailyForecasts") val dailyForecasts: List<DailyForecast>
)

data class Headline(
    @SerializedName("EffectiveDate") val effectiveDate: String,
    @SerializedName("Text") val text: String,
)

data class DailyForecast(
    @SerializedName("Date") val date: String,
    @SerializedName("Temperature") val temperature: Temperature,
    @SerializedName("Day") val day: Weather,
    @SerializedName("Night") val night: Weather,
)

data class Temperature(
    @SerializedName("Minimum") val minimum: TemperatureValue,
    @SerializedName("Maximum") val maximum: TemperatureValue
)

data class TemperatureValue(
    @SerializedName("Value") val value: Double,
    @SerializedName("Unit") val unit: String,
)

data class Weather(
    @SerializedName("IconPhrase") val iconPhrase: String,
)
