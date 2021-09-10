package com.castcle.networking.service.interceptor

interface AppRefreshTokenFailedListener {

    fun onRefreshTokenFailed(error: Throwable)
}
