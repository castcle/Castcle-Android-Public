package com.castcle.networking.service.providers

import android.content.Context
import com.castcle.authen_android.data.storage.SecureStorage
import com.castcle.networking.service.authenticator.TokenRefresher
import com.castcle.networking.service.common.secrets.ApiEndPointUrl
import com.castcle.session_memory.SessionManagerRepository
import com.castcle.session_memory.model.SessionEnvironment
import retrofit2.Retrofit

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
//  Created by sklim on 20/8/2021 AD at 15:06.

object CoreApi {

    fun retrofitService(
        context: Context,
        apiEndPointUrl: ApiEndPointUrl,
        sessionVariable: SessionEnvironment,
        tokenRefresher: TokenRefresher,
        requiredAuthenticated: Boolean = true,
        secureStorage: SecureStorage,
        sessionManagerRepository: SessionManagerRepository
    ): Retrofit {
        sessionManagerRepository.setSessionEnvironment(sessionVariable)
        return RetrofitServiceProviders.getRetrofitApi(
            context = context,
            apiEndPointUrl = apiEndPointUrl,
            sessionManagerRepository = sessionManagerRepository,
            requiredAuthenticated = requiredAuthenticated,
            tokenRefresher = tokenRefresher,
            secureStorage = secureStorage
        )
    }

    fun retrofitService(
        context: Context,
        apiEndPointUrl: ApiEndPointUrl,
        sessionVariable: SessionEnvironment,
        requiredAuthenticated: Boolean = true,
        secureStorage: SecureStorage,
        sessionManagerRepository: SessionManagerRepository?
    ): Retrofit {
        sessionManagerRepository?.setSessionEnvironment(sessionVariable)
        return RetrofitServiceProviders.getRetrofitApi(
            context = context,
            apiEndPointUrl = apiEndPointUrl,
            sessionManagerRepository = sessionManagerRepository,
            requiredAuthenticated = requiredAuthenticated,
            tokenRefresher = null,
            secureStorage = secureStorage
        )
    }

    fun retrofitService(
        context: Context,
        apiEndPointUrl: ApiEndPointUrl,
        sessionVariable: SessionEnvironment,
        requiredAuthenticated: Boolean = false,
        sessionManagerRepository: SessionManagerRepository?
    ): Retrofit {
        sessionManagerRepository?.setSessionEnvironment(sessionVariable)
        return RetrofitServiceProviders.getRetrofitApi(
            context = context,
            apiEndPointUrl = apiEndPointUrl,
            sessionManagerRepository = sessionManagerRepository,
            requiredAuthenticated = requiredAuthenticated,
            tokenRefresher = null,
            secureStorage = null
        )
    }
}
