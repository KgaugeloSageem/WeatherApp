package com.sure.weatherapp.mapview.service.models

import com.google.gson.annotations.SerializedName

data class LocationKeyResponse(
    @SerializedName("Key") val key: String,
    @SerializedName("LocalizedName") val localizedName: String,
    @SerializedName("Country") val country: Country,
    @SerializedName("AdministrativeArea") val administrativeArea: AdministrativeArea,
    @SerializedName("SupplementalAdminAreas") val supplementalAdminAreas: List<SupplementalAdminArea>
)