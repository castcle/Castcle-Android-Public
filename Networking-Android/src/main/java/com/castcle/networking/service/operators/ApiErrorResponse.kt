package com.castcle.networking.service.operators

import com.google.gson.annotations.SerializedName

data class ApiErrorResponse(
    @SerializedName("statusCode") val statusCode: Int? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null,
)
