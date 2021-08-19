package com.castcle.networking_android.service.common.secrets

import com.castcle.networking_android.service.common.secretes.ApiEndPointUrl

internal class ApiEndPointUrlImpl : ApiEndPointUrl {
    override val value: String
        get() = "https://api-stg.castcle.com/"
}
