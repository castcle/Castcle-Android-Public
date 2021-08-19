package com.castcle.networking_android.service.operators

object ApiOperators {

    fun <T> mobileApiError(): MobileApiErrorOperator<T> {
        return MobileApiErrorOperator()
    }
}
