package com.castcle.networking.service.authenticator

import com.castcle.networking.service.common.AUTHORIZATION_HEADER
import com.castcle.networking.service.interceptor.AppTokenExpiredDelegate
import com.castcle.networking.service.response.OAuthResponse
import com.castcle.session_memory.SessionManagerRepository
import com.castcle.session_memory.model.SessionEnvironment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import okhttp3.*

//  Copyright (c) 2021, Castcle and/or its affiliates. All rights reserved.
//  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//
//  This code is free software; you can redistribute it and/or modify it
//  under the terms of the GNU General Public License version 3 only, as
//  published by the Free Software Foundation.
//
//  This code is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
//  version 3 for more details (a copy is included in the LICENSE file that
//  accompanied this code).
//
//  You should have received a copy of the GNU General Public License version
//  3 along with this work; if not, write to the Free Software Foundation,
//  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
//
//  Please contact Castcle, 22 Phet Kasem 47/2 Alley, Bang Khae, Bangkok,
//  Thailand 10160, or visit www.castcle.com if you need additional information
//  or have any questions.
//
//
//  Created by sklim on 20/8/2021 AD at 13:22.

internal class ApplicationRequestAuthenticator(
    private val tokenRefresher: TokenRefresher,
    private val sessionManagerRepository: SessionManagerRepository?,
    var okHttpClient: OkHttpClient? = null
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val failedToken = response.request.header(AUTHORIZATION_HEADER)
        var refreshTokenResponse: OAuthResponse? = null
        var newToken = ""
        try {
            refreshTokenResponse = tokenRefresher
                .refreshToken()
                .doOnError {
                    FirebaseCrashlytics.getInstance()
                        .setCustomKey(
                            LOG_APPLICATION_REQUEST_AUTHENTICATOR,
                            it.message.toString()
                        )
                }
                .blockingGet()
            newToken = refreshTokenResponse.accessToken
        } catch (e: Exception) {
            okHttpClient?.dispatcher?.cancelAll()
            AppTokenExpiredDelegate.requestTokenFailed(e)
            return null
        }

        if (newToken.isEmpty() || newToken == failedToken) {
            return null
        }

        if (refreshTokenResponse != null) {
            sessionManagerRepository?.setSessionToken(
                SessionEnvironment.SessionToken(
                    refreshTokenResponse.accessToken
                )
            )
        }

        // Retry this failed request (401) with the new token
        return response.request
            .newBuilder()
            .header(AUTHORIZATION_HEADER, "$TOKEN_TYPE $newToken")
            .build()
    }
}

const val LOG_APPLICATION_REQUEST_AUTHENTICATOR = "log-api-ApplicationRequestAuthenticator"
private const val TOKEN_TYPE = "Bearer"