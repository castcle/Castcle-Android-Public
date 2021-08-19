package com.castcle.networking_android.service.operators

import com.google.gson.annotations.SerializedName

data class ApiErrorResponse(
    @SerializedName("type") val type: String? = null,
    @SerializedName("code") val code: String? = null,
    @SerializedName("message") val message: String? = null,
)
