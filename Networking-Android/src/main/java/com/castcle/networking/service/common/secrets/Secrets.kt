package com.castcle.networking.service.common.secrets

object Secrets {
    val apiEndpointUrl: ApiEndPointUrl
        get() = ApiEndPointUrlImpl()
}
