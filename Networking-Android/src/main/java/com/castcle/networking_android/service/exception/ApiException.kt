package com.castcle.networking_android.service.exception

class ApiException(
    override val message: String?,
    val code: String,
    val type: String?
) : Throwable()

const val PARTIAL_PROCESSING = "PARTIAL_PROCESSING"
