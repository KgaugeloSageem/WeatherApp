package com.sure.weatherapp.mapview.service.models

import com.google.gson.annotations.SerializedName

data class WeatherForecastResponse(
    @SerializedName("Headline") val headline: Headline,
    @SerializedName("DailyForecasts") val dailyForecasts: List<DailyForecast>
) {
    companion object {
        fun WeatherForecastResponse.toCurrentDayForecastDetails(locationName: String): WeatherForecastDetails {
            return WeatherForecastDetails(
                header = headline.text,
                locationName = locationName,
                date = dailyForecasts[0].date,
                minTemp = "${dailyForecasts[0].temperature.minimum.value} ${dailyForecasts[0].temperature.minimum.unit}",
                maxTemp = "${dailyForecasts[0].temperature.maximum.value} ${dailyForecasts[0].temperature.minimum.unit}",
                dayDescription = dailyForecasts[0].day.iconPhrase,
                nightDescription = dailyForecasts[0].night.iconPhrase
            )
        }

        fun WeatherForecastResponse.getFiveDayForecastList(locationName: String): List<WeatherForecastDetails> {
            return dailyForecasts.map {
                WeatherForecastDetails(
                    header = headline.text,
                    locationName = locationName,
                    date = it.date,
                    minTemp = "${it.temperature.minimum.value} ${it.temperature.minimum.unit}",
                    maxTemp = "${it.temperature.maximum.value} ${it.temperature.minimum.unit}",
                    dayDescription = it.day.iconPhrase,
                    nightDescription = it.night.iconPhrase
                )
            }
        }
    }
}

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

data class WeatherForecastDetails(
    val header: String,
    val locationName: String,
    val date: String,
    val minTemp: String,
    val maxTemp: String,
    val dayDescription: String,
    val nightDescription: String
)
