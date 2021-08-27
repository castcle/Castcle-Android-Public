package com.castcle.networking.service.operators

object ApiOperators {

    fun <T> mobileApiError(): MobileApiErrorOperator<T> {
        return MobileApiErrorOperator()
    }
}
