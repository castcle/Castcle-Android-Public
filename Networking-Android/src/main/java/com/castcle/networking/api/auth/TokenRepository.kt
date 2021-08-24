package com.castcle.networking.api.auth

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.networking.api.auth.request.DeviceUUIDRequest
import com.castcle.networking.api.nonauthen.NonAuthenticationApi
import com.castcle.networking.api.response.toOAuthResponse
import com.castcle.networking.service.authenticator.TokenRefresher
import com.castcle.networking.service.operators.ApiOperators
import com.castcle.networking.service.response.OAuthResponse
import com.castcle.session_memory.SessionManagerRepository
import com.castcle.session_memory.model.SessionEnvironment
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

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
//  Created by sklim on 20/8/2021 AD at 13:57.

interface TokenRepository : TokenRefresher {
    fun getLoginGuest(): Completable
}

class TokenRepositoryImpl @Inject constructor(
    private val context: Context,
    private val secureStorage: SecureStorage,
    private val nonAuthenticationApi: NonAuthenticationApi,
    private val sessionManagerRepository: SessionManagerRepository
) : TokenRepository {

    override fun getLoginGuest(): Completable {
        val deviceUUid = DeviceUUIDRequest(getDeviceId())
        return nonAuthenticationApi
            .getLoginGuest(deviceUUid)
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
            .doOnSuccess {
                updateAccessToken(it.toOAuthResponse())
            }
            .ignoreElement()
    }

    override fun refreshToken(): Single<OAuthResponse> {
        return nonAuthenticationApi
            .getRefreshToken()
            .retry(2)
            .lift(ApiOperators.mobileApiError())
            .firstOrError()
            .map { it.toOAuthResponse() }
            .doOnSuccess { updateAccessToken(it) }
    }

    private fun updateAccessToken(oAuthResponse: OAuthResponse) {
        secureStorage.apply {
            userAccessToken = oAuthResponse.accessToken
        }
        updateSessionToken(oAuthResponse)
    }

    private fun updateSessionToken(response: OAuthResponse) {
        sessionManagerRepository.setSessionToken(
            SessionEnvironment.SessionToken(
                response.accessToken
            )
        )
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        return Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
    }
}

