package com.castcle.networking_android.service.common.secrets

import com.castcle.networking_android.service.common.secrets.ApiEndPointUrl

internal class ApiEndPointUrlImpl : ApiEndPointUrl {
    override val value: String
        get() = "https://api-dev.castcle.com/"
}
