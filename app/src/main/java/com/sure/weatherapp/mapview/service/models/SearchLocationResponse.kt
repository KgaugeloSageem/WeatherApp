package com.sure.weatherapp.mapview.service.models

import com.google.gson.annotations.SerializedName

data class SearchLocationResponse(
    @SerializedName("Key") val key: String,
    @SerializedName("LocalizedName") val localizedName: String,
    @SerializedName("Country") val country: Country,
    @SerializedName("AdministrativeArea") val administrativeArea: AdministrativeArea,
    @SerializedName("GeoPosition") val geoPosition: GeoPosition,
    @SerializedName("ParentCity") val parentCity: ParentCity,
    @SerializedName("SupplementalAdminAreas") val supplementalAdminAreas: List<SupplementalAdminArea>
)

data class Country(
    @SerializedName("LocalizedName") val localizedName: String
)

data class AdministrativeArea(
    @SerializedName("LocalizedName") val localizedName: String
)

data class GeoPosition(
    @SerializedName("Latitude") val latitude: Double,
    @SerializedName("Longitude") val longitude: Double
)

data class ParentCity(
    @SerializedName("LocalizedName") val localizedName: String
)

data class SupplementalAdminArea(
    @SerializedName("LocalizedName") val localizedName: String
)
