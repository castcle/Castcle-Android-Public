package com.castcle.networking.service.exception

class ApiException(
    override val message: String?,
    val code: String,
    val statusCode: Int?,
    val error: String?
) : Throwable()

const val PARTIAL_PROCESSING = "PARTIAL_PROCESSING"
