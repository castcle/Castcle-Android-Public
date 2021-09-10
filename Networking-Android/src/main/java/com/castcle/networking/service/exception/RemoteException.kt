package com.castcle.networking.service.exception

data class RemoteException(val code: Int = 500, val msg: String? = "") : Throwable(message = msg)
