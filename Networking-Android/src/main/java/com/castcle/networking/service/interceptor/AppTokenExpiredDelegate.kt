package com.castcle.networking.service.interceptor


object AppTokenExpiredDelegate {

    private val appRefreshTokenFailedListeners:
        MutableList<AppRefreshTokenFailedListener> = mutableListOf()

    fun bindAppTokenExpiredListener(listener: AppRefreshTokenFailedListener) {
        appRefreshTokenFailedListeners.add(listener)
    }

    fun unbindAppTokenExpiredListener(listener: AppRefreshTokenFailedListener) {
        appRefreshTokenFailedListeners.remove(listener)
    }

    @Synchronized
    internal fun requestTokenFailed(error: Throwable) {
        val iterator = appRefreshTokenFailedListeners.iterator()
        while (iterator.hasNext()) {
            iterator.next().onRefreshTokenFailed(error)
        }
    }
}
